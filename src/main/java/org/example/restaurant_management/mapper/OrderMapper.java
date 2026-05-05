// OrderMapper.java
package org.example.restaurant_management.mapper;

import org.example.restaurant_management.dto.response.OrderItemResponse;
import org.example.restaurant_management.dto.response.OrderResponse;
import org.example.restaurant_management.dto.response.PaymentResponse;
import org.example.restaurant_management.entity.Order;
import org.example.restaurant_management.entity.OrderItem;
import org.example.restaurant_management.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(source = "table.id", target = "tableId")
    @Mapping(source = "table.tableNumber", target = "tableNumber")
    @Mapping(source = "createdBy.id", target = "createdById")
    @Mapping(source = "createdBy.username", target = "createdByName")
    @Mapping(target = "items", ignore = true) // sẽ set thủ công trong service
    OrderResponse toOrderResponse(Order order);

    @Mapping(source = "menuItem.id", target = "menuItemId")
    @Mapping(source = "menuItem.name", target = "menuItemName")
    @Mapping(target = "subtotal", expression = "java(item.getPrice() * item.getQuantity())")
    OrderItemResponse toOrderItemResponse(OrderItem item);

    @Mapping(source = "order.id", target = "orderId")
    PaymentResponse toPaymentResponse(Payment payment);
}