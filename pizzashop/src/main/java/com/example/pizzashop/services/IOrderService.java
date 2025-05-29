package com.example.pizzashop.services;

import com.example.pizzashop.dtos.OrderDTO;
import com.example.pizzashop.exceptions.DataNotFoundException;
import com.example.pizzashop.models.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IOrderService {
    Order createOrder (OrderDTO orderDTO) throws Exception;
    Order getOrder(Long id);
    Order updateOrder(long id, OrderDTO orderDTO) throws Exception;
    void deleteOrder(long id) ;
    List<Order> findByUserId(Long userId);
    Page<Order> getOrdersByKeyword(String keyword, Pageable pageable);

    Order updateTierInMembership(String phone);
}
