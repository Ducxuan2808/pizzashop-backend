package com.example.pizzashop.services;

import com.example.pizzashop.dtos.OrderDetailDTO;
import com.example.pizzashop.exceptions.DataNotFoundException;
import com.example.pizzashop.models.OrderDetail;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IOrderDetailService {
    OrderDetail createOrderDetail(OrderDetailDTO newOrderDetail) throws Exception;
    OrderDetail getOrderDetail(Long id) throws DataNotFoundException;
    OrderDetail updateOrderDetail(Long id, OrderDetailDTO newOrderDetailData) throws DataNotFoundException;
    void deleteById(Long id);
    List<OrderDetail> findByOrderId(Long orderId);
}
