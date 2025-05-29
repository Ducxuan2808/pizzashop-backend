package com.example.pizzashop.services;

import com.example.pizzashop.dtos.OrderDetailDTO;
import com.example.pizzashop.exceptions.DataNotFoundException;
import com.example.pizzashop.models.*;
import com.example.pizzashop.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderDetailService implements  IOrderDetailService{
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final PizzaRepository pizzaRepository;
    private final SizeRepository sizeRepository;
    private final BaseTypeRepository baseTypeRepository;

    @Override
    @Transactional
    public OrderDetail createOrderDetail(OrderDetailDTO orderDetailDTO) throws Exception {
        Order order = orderRepository.findById(orderDetailDTO.getOrderId())
                .orElseThrow(()->new DataNotFoundException("Không tìm thấy đơn hàng có id: "+ orderDetailDTO.getOrderId()));
        Pizza pizza = pizzaRepository.findById(orderDetailDTO.getPizzaId())
                .orElseThrow(()->new DataNotFoundException("Không tìm thấy pizza có id: "+ orderDetailDTO.getPizzaId()));
        Size size = sizeRepository.findById(orderDetailDTO.getSizeId())
                .orElseThrow(()->new DataNotFoundException("Không tìm thấy size có id: "+ orderDetailDTO.getSizeId()));
        BaseType baseType = baseTypeRepository.findById(orderDetailDTO.getBaseId())
                .orElseThrow(()->new DataNotFoundException("Không tìm thấy loại có id: "+ orderDetailDTO.getBaseId()));
        OrderDetail orderDetail = OrderDetail.builder()
                .order(order)
                .pizza(pizza)
                .size(size)
                .baseType(baseType)
                .quantity(orderDetailDTO.getQuantity())
                .price(orderDetailDTO.getPrice())
                .note(orderDetailDTO.getNote())
                .build();
        orderDetailRepository.save(orderDetail);
//        order.setTotalPrice(order.getTotalPrice()+orderDetail.getPrice());
//        orderRepository.save(order);
        return orderDetail;
    }

    @Override
    public OrderDetail getOrderDetail(Long id) throws DataNotFoundException {
        return orderDetailRepository.findById(id)
                .orElseThrow(()->new DataNotFoundException("Không tìm thấy chi tiết đơn hàng có id: "+id));
    }

    @Override
    public OrderDetail updateOrderDetail(Long id, OrderDetailDTO orderDetailDTO) throws DataNotFoundException {
        //tim xem orderDetail ton tai khong
        OrderDetail existingOrderDetail = orderDetailRepository.findById(id)
                .orElseThrow(()->new DataNotFoundException("Cannot find order detail with id: "+id));
        Order existingOrder = orderRepository.findById(orderDetailDTO.getOrderId())
                .orElseThrow(()->new DataNotFoundException("Không tìm thấy đơn hàng có id: "+orderDetailDTO.getOrderId()));
        Pizza existingPizza = pizzaRepository.findById(orderDetailDTO.getPizzaId())
                .orElseThrow(()->new DataNotFoundException("Không tìm thấy pizza có id: "+orderDetailDTO.getPizzaId()));
        Size size = sizeRepository.findById(orderDetailDTO.getSizeId())
                .orElseThrow(()->new DataNotFoundException("Không tìm thấy size có id: "+ orderDetailDTO.getSizeId()));
        BaseType baseType = baseTypeRepository.findById(orderDetailDTO.getBaseId())
                .orElseThrow(()->new DataNotFoundException("Không tìm thấy loại có id: "+ orderDetailDTO.getBaseId()));
        existingOrderDetail.setSize(size);
        existingOrderDetail.setBaseType(baseType);
        existingOrderDetail.setQuantity(orderDetailDTO.getQuantity());
        existingOrderDetail.setPrice(orderDetailDTO.getPrice());
        existingOrderDetail.setNote(orderDetailDTO.getNote());
        existingOrderDetail.setOrder(existingOrder);
        existingOrderDetail.setPizza(existingPizza);
        orderDetailRepository.save(existingOrderDetail);

        return existingOrderDetail;
    }

    @Override
    public void deleteById(Long id) {
        orderDetailRepository.deleteById(id);
    }

    @Override
    public List<OrderDetail> findByOrderId(Long orderId) {
        return orderDetailRepository.findByOrderId(orderId);
    }
}
