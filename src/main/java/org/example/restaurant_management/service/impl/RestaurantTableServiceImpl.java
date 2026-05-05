package org.example.restaurant_management.service.impl;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.restaurant_management.dto.request.RestaurantTableRequest;
import org.example.restaurant_management.dto.response.RestaurantTableResponse;
import org.example.restaurant_management.entity.Restaurant;
import org.example.restaurant_management.entity.RestaurantTable;
import org.example.restaurant_management.exception.AppException;
import org.example.restaurant_management.exception.ErrorCode;
import org.example.restaurant_management.mapper.RestaurantTableMapper;
import org.example.restaurant_management.repository.RestaurantRepository;
import org.example.restaurant_management.repository.RestaurantTableRepository;
import org.example.restaurant_management.service.RestaurantTableService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RestaurantTableServiceImpl implements RestaurantTableService {
    RestaurantTableRepository restaurantTableRepository;

    RestaurantRepository restaurantRepository;

    RestaurantTableMapper restaurantTableMapper;


    @Transactional
    @Override
    public RestaurantTableResponse createRestaurantTable(Long restaurantId, RestaurantTableRequest request) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() ->  new AppException(ErrorCode.RESTAURANT_NOT_EXISTED));

        RestaurantTable restaurantTable = restaurantTableMapper.toRestaurantTable(request);
        restaurantTable.setRestaurant(restaurant);
        restaurantTable.setStatus(RestaurantTable.TableStatus.AVAILABLE);

        restaurantTableRepository.save(restaurantTable);

        return restaurantTableMapper.toRestaurantTableResponse(restaurantTable);
    }

    @Transactional
    @Override
    public RestaurantTableResponse updateRestaurantTable(Long restaurantId, Long tableId,
                                                         RestaurantTableRequest request) {
        RestaurantTable table = restaurantTableRepository.findById(tableId)
                .orElseThrow(() -> new AppException(ErrorCode.TABLE_NOT_EXISTED));

        // Đảm bảo table thuộc đúng restaurant trên URL
        if (!table.getRestaurant().getId().equals(restaurantId)) {
            throw new AppException(ErrorCode.TABLE_NOT_EXISTED);
        }

        restaurantTableMapper.updateRestaurantTable(table, request);
        restaurantTableRepository.save(table);

        return restaurantTableMapper.toRestaurantTableResponse(table);
    }

    @Transactional
    @Override
    public void deleteRestaurantTable(Long restaurantId, Long tableId) {
        RestaurantTable table = restaurantTableRepository.findById(tableId)
                .orElseThrow(() -> new AppException(ErrorCode.TABLE_NOT_EXISTED));

        if (!table.getRestaurant().getId().equals(restaurantId)) {
            throw new AppException(ErrorCode.TABLE_NOT_EXISTED);
        }

        // Tùy nghiệp vụ: chặn xóa nếu đang có khách
        if (table.getStatus() == RestaurantTable.TableStatus.OCCUPIED) {
            throw new AppException(ErrorCode.TABLE_IN_USE);
        }

        restaurantTableRepository.delete(table);
    }

    @Override
    public List<RestaurantTableResponse> getTablesByRestaurant(Long restaurantId) {
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new AppException(ErrorCode.RESTAURANT_NOT_EXISTED);
        }

        return restaurantTableRepository.findByRestaurant_Id(restaurantId)
                .stream()
                .map(restaurantTableMapper::toRestaurantTableResponse)
                .toList();
    }

    @Override
    public RestaurantTableResponse getTableById(Long restaurantId, Long tableId) {
        RestaurantTable table = restaurantTableRepository.findById(tableId)
                .orElseThrow(() -> new AppException(ErrorCode.TABLE_NOT_EXISTED));

        if (!table.getRestaurant().getId().equals(restaurantId)) {
            throw new AppException(ErrorCode.TABLE_NOT_EXISTED);
        }

        return restaurantTableMapper.toRestaurantTableResponse(table);
    }
}
