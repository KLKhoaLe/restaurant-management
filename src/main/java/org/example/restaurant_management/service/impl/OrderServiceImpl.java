package org.example.restaurant_management.service.impl;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.restaurant_management.dto.request.AddOrderItemsRequest;
import org.example.restaurant_management.dto.request.CreateOrderRequest;
import org.example.restaurant_management.dto.request.PaymentRequest;
import org.example.restaurant_management.dto.request.UpdateOrderItemRequest;
import org.example.restaurant_management.dto.response.OrderItemResponse;
import org.example.restaurant_management.dto.response.OrderResponse;
import org.example.restaurant_management.dto.response.PaymentResponse;
import org.example.restaurant_management.entity.*;
import org.example.restaurant_management.exception.AppException;
import org.example.restaurant_management.exception.ErrorCode;
import org.example.restaurant_management.mapper.OrderMapper;
import org.example.restaurant_management.repository.*;
import org.example.restaurant_management.service.OrderService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderServiceImpl implements OrderService {

    OrderRepository orderRepository;
    OrderItemRepository orderItemRepository;
    PaymentRepository paymentRepository;
    RestaurantTableRepository restaurantTableRepository;
    MenuItemRepository menuItemRepository;
    UserRepository userRepository;
    OrderMapper orderMapper;


    private static final Set<Order.OrderStatus> ACTIVE_STATUSES = Set.of(
            Order.OrderStatus.OPEN,
            Order.OrderStatus.SERVING,
            Order.OrderStatus.SERVED
    );

    // ============================================================ //
    //  ORDER LIFECYCLE
    // ============================================================ //

    @Transactional
    @Override
    public OrderResponse createOrder(Long restaurantId, Long userId, CreateOrderRequest request) {

        // 1. Lấy & verify bàn thuộc đúng restaurant
        RestaurantTable table = restaurantTableRepository.findById(request.getTableId())
                .orElseThrow(() -> new AppException(ErrorCode.TABLE_NOT_EXISTED));

        if (!table.getRestaurant().getId().equals(restaurantId)) {
            throw new AppException(ErrorCode.TABLE_NOT_EXISTED);
        }

        // 2. Chặn nếu bàn đã có order OPEN
        if (orderRepository.existsByTable_IdAndStatusIn(table.getId(), ACTIVE_STATUSES)) {
            throw new AppException(ErrorCode.TABLE_HAS_ACTIVE_ORDER);
        }

        // 3. Lấy user tạo order
        User creator = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // 4. Tạo order
        Order order = Order.builder()
                .restaurant(table.getRestaurant())
                .table(table)
                .createdBy(creator)
                .status(Order.OrderStatus.OPEN)
                .totalAmount(0.0)
                .createdAt(LocalDateTime.now())
                .build();
        orderRepository.save(order);

        // 5. Update trạng thái bàn
        table.setStatus(RestaurantTable.TableStatus.OCCUPIED);
        restaurantTableRepository.save(table);

        return buildOrderResponse(order);
    }

    @Override
    public OrderResponse getOrderById(Long restaurantId, Long orderId) {
        Order order = findOrderInRestaurant(restaurantId, orderId);
        return buildOrderResponse(order);
    }

    @Override
    public OrderResponse getActiveOrderByTable(Long restaurantId, Long tableId) {
        // Verify bàn thuộc restaurant
        RestaurantTable table = restaurantTableRepository.findById(tableId)
                .orElseThrow(() -> new AppException(ErrorCode.TABLE_NOT_EXISTED));
        if (!table.getRestaurant().getId().equals(restaurantId)) {
            throw new AppException(ErrorCode.TABLE_NOT_EXISTED);
        }

        Order order = orderRepository
                .findByTable_IdAndStatusIn(tableId, ACTIVE_STATUSES)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

        return buildOrderResponse(order);
    }

    @Override
    public List<OrderResponse> getOrdersByRestaurant(Long restaurantId) {
        return orderRepository.findByRestaurant_Id(restaurantId)
                .stream()
                .map(this::buildOrderResponse)
                .toList();
    }

    @Transactional
    @Override
    public void cancelOrder(Long restaurantId, Long orderId) {
        Order order = findOrderInRestaurant(restaurantId, orderId);

        if (order.getStatus() != Order.OrderStatus.OPEN) {
            throw new AppException(ErrorCode.ORDER_NOT_OPEN);
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        orderRepository.save(order);

        // Giải phóng bàn
        RestaurantTable table = order.getTable();
        table.setStatus(RestaurantTable.TableStatus.AVAILABLE);
        restaurantTableRepository.save(table);
    }

    @Transactional
    @Override
    public OrderResponse startServing(Long restaurantId, Long orderId) {
        Order order = findOrderInRestaurant(restaurantId, orderId);

        // Chỉ cho phép OPEN → SERVING
        if (order.getStatus() != Order.OrderStatus.OPEN) {
            throw new AppException(ErrorCode.INVALID_ORDER_STATUS_TRANSITION);
        }

        order.setStatus(Order.OrderStatus.SERVING);
        orderRepository.save(order);

        return buildOrderResponse(order);
    }

    @Transactional
    @Override
    public OrderResponse markServed(Long restaurantId, Long orderId) {
        Order order = findOrderInRestaurant(restaurantId, orderId);

        // Cho phép từ OPEN hoặc SERVING → SERVED
        if (order.getStatus() != Order.OrderStatus.OPEN
                && order.getStatus() != Order.OrderStatus.SERVING) {
            throw new AppException(ErrorCode.INVALID_ORDER_STATUS_TRANSITION);
        }

        order.setStatus(Order.OrderStatus.SERVED);
        orderRepository.save(order);

        return buildOrderResponse(order);
    }

    // ============================================================ //
    //  ORDER ITEMS
    // ============================================================ //

    @Transactional
    @Override
    public OrderResponse addItems(Long restaurantId, Long orderId, AddOrderItemsRequest request) {
        Order order = findOrderInRestaurant(restaurantId, orderId);
        ensureActive(order);

        for (AddOrderItemsRequest.OrderItemLine line : request.getItems()) {
            MenuItem menuItem = menuItemRepository.findById(line.getMenuItemId())
                    .orElseThrow(() -> new AppException(ErrorCode.MENU_ITEM_NOT_EXISTED));

            // Menu item phải thuộc cùng restaurant
            if (!menuItem.getRestaurant().getId().equals(restaurantId)) {
                throw new AppException(ErrorCode.MENU_ITEM_NOT_EXISTED);
            }

            OrderItem item = OrderItem.builder()
                    .order(order)
                    .menuItem(menuItem)
                    .quantity(line.getQuantity())
                    .price(menuItem.getPrice())   // SNAPSHOT giá hiện tại
                    .note(line.getNote())
                    .build();
            orderItemRepository.save(item);
        }

        recalculateTotal(order);
        return buildOrderResponse(order);
    }

    @Transactional
    @Override
    public OrderResponse updateItem(Long restaurantId, Long orderId, Long itemId,
                                    UpdateOrderItemRequest request) {
        Order order = findOrderInRestaurant(restaurantId, orderId);
        ensureActive(order);

        OrderItem item = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_ITEM_NOT_EXISTED));

        if (!item.getOrder().getId().equals(orderId)) {
            throw new AppException(ErrorCode.ORDER_ITEM_NOT_EXISTED);
        }

        if (request.getQuantity() != null) item.setQuantity(request.getQuantity());
        if (request.getNote() != null) item.setNote(request.getNote());

        orderItemRepository.save(item);
        recalculateTotal(order);

        return buildOrderResponse(order);
    }

    @Transactional
    @Override
    public OrderResponse removeItem(Long restaurantId, Long orderId, Long itemId) {
        Order order = findOrderInRestaurant(restaurantId, orderId);
        ensureActive(order);

        OrderItem item = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_ITEM_NOT_EXISTED));

        if (!item.getOrder().getId().equals(orderId)) {
            throw new AppException(ErrorCode.ORDER_ITEM_NOT_EXISTED);
        }

        orderItemRepository.delete(item);
        recalculateTotal(order);

        return buildOrderResponse(order);
    }

    // ============================================================ //
    //  PAYMENT
    // ============================================================ //

    @Transactional
    @Override
    public PaymentResponse payOrder(Long restaurantId, Long orderId, PaymentRequest request) {
        Order order = findOrderInRestaurant(restaurantId, orderId);
        ensureActive(order);

        // Optional: kiểm tra số tiền trùng với total
        if (request.getAmount() == null
                || Math.abs(request.getAmount() - order.getTotalAmount()) > 0.01) {
            throw new AppException(ErrorCode.PAYMENT_AMOUNT_MISMATCH);
        }

        // 1. Tạo payment
        Payment payment = Payment.builder()
                .order(order)
                .amount(request.getAmount())
                .paymentMethod(request.getPaymentMethod())
                .paidAt(LocalDateTime.now())
                .build();
        paymentRepository.save(payment);

        // 2. Đóng order
        order.setStatus(Order.OrderStatus.PAID);
        orderRepository.save(order);

        // 3. Giải phóng bàn
        RestaurantTable table = order.getTable();
        table.setStatus(RestaurantTable.TableStatus.AVAILABLE);
        restaurantTableRepository.save(table);

        return orderMapper.toPaymentResponse(payment);
    }

    // ============================================================ //
    //  PRIVATE HELPERS
    // ============================================================ //

    private Order findOrderInRestaurant(Long restaurantId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

        if (!order.getRestaurant().getId().equals(restaurantId)) {
            throw new AppException(ErrorCode.ORDER_NOT_EXISTED);
        }

        return order;
    }

    private void ensureActive(Order order) {
        if (order.getStatus().isClosed()) {
            throw new AppException(ErrorCode.ORDER_ALREADY_CLOSED);
        }
    }

    private void recalculateTotal(Order order) {
        List<OrderItem> items = orderItemRepository.findByOrder_Id(order.getId());
        double total = items.stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();
        order.setTotalAmount(total);
        orderRepository.save(order);
    }

    private OrderResponse buildOrderResponse(Order order) {
        OrderResponse response = orderMapper.toOrderResponse(order);

        List<OrderItemResponse> itemResponses = orderItemRepository
                .findByOrder_Id(order.getId())
                .stream()
                .map(orderMapper::toOrderItemResponse)
                .toList();

        response.setItems(itemResponses);
        return response;
    }
}