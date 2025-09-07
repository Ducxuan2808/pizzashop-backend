package com.example.pizzashop.services;

import com.example.pizzashop.dtos.CartItemDTO;
import com.example.pizzashop.dtos.MembershipDTO;
import com.example.pizzashop.dtos.OrderDTO;
import com.example.pizzashop.exceptions.DataNotFoundException;
import com.example.pizzashop.models.*;
import com.example.pizzashop.repositories.*;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService{
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private ModelMapper modelMapper;
    private final OrderDetailRepository orderDetailRepository;
    private final PizzaRepository pizzaRepository;
    private final SizeRepository sizeRepository;
    private final BaseTypeRepository baseTypeRepository;
    private final MembershipRepository membershipRepository;
    private final MembershipService membershipService;
    @Override
    @Transactional
    public Order createOrder(OrderDTO orderDTO) throws Exception {
        //tim xem user'id co ton tai khong

        User user =  userRepository
                .findById(orderDTO.getUserId())
                .orElse(null);

        //convert orderDTO => Order
        //dung thu vien ModelMapper
//        modelMapper.typeMap(OrderDTO.class, Order.class)
//                .addMappings(mapper ->mapper.skip(Order::setId));
        Order order = new Order();
//        modelMapper.map(orderDTO,order);
        order.setFullName(orderDTO.getFullName());
        order.setOrderType(orderDTO.getOrderType());
        order.setDiscountAmount(orderDTO.getDiscountAmount());
        order.setDeliveryAddress(orderDTO.getDeliveryAddress());
        order.setDeliveryPhone(orderDTO.getDeliveryPhone());
        order.setTable(orderDTO.getTable());
        order.setNote(orderDTO.getNote());
        order.setPaymentMethod(orderDTO.getPaymentMethod());
        order.setEmail(orderDTO.getEmail());

        if(user != null && user.getRole().getId() == 2){
            order.setPhoneOrder(user.getPhone());
        }else{
            order.setPhoneOrder(orderDTO.getPhoneOrder());
        }

        order.setUser(user);
        order.setOrderTime(orderDTO.getOrderTime());
        order.setStatus(Order.STATUS_PENDING);
        //kiem tra shipping date phai >= ngay hom qua
        java.util.Date shippingTime = orderDTO.getShippingTime() == null
                ? Date.valueOf(LocalDate.now()) : orderDTO.getShippingTime();
        if(shippingTime.before(Date.valueOf(LocalDate.now()))){
            throw new DataNotFoundException("Data must be at least today|");
        }
        order.setShippingTime(shippingTime);
        order.setActive(true);
        order.setTotalPrice(orderDTO.getTotalPrice());

        orderRepository.save(order);

        List<OrderDetail> orderDetails = new ArrayList<>();
        for(CartItemDTO cartItemDTO: orderDTO.getCartItems()){
            //tao 1 doi tuong  OrderDetail tu CartItemDTO
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);

            Long pizzaId = cartItemDTO.getPizzaId();
            Long sizeId = cartItemDTO.getSizeId();
            Long typeId = cartItemDTO.getBaseId();
            Float price = cartItemDTO.getPrice();
            String note = cartItemDTO.getNote();
            int quantity = cartItemDTO.getQuantity();

            //tim thong tin san pham tu csdl
            Pizza pizza = pizzaRepository.findById(pizzaId)
                    .orElseThrow(()->new DataNotFoundException("Pizza not found with id: "+pizzaId));
            Size size = sizeRepository.findById(sizeId)
                    .orElseThrow(()->new DataNotFoundException("Không tìm thấy size có id: "+ sizeId));
            BaseType baseType = baseTypeRepository.findById(typeId)
                    .orElseThrow(()->new DataNotFoundException("Không tìm thấy loại có id: "+ typeId));
            //Dat thong tin cho OrderDetail
            orderDetail.setPizza(pizza);
            orderDetail.setQuantity(quantity);
            orderDetail.setSize(size);
            orderDetail.setBaseType(baseType);
            orderDetail.setPrice(price);
            order.setNote(note);

            orderDetails.add(orderDetail);

        }
        //Luu vao CSDL
        orderDetailRepository.saveAll(orderDetails);
        return order;
    }

    @Override
    public Order getOrder(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Order updateOrder(long id, OrderDTO orderDTO) throws Exception {
        Order order = orderRepository.findById(id).orElseThrow(()->
                new DataNotFoundException("Cannot find order with id: "+id));
//        User exsistingUser = userRepository.findById(orderDTO.getUserId()).orElse(null);
//        modelMapper.typeMap(OrderDTO.class,Order.class)
//                .addMappings(mapper->mapper.skip(Order::setId));
//        modelMapper.map(orderDTO,order);
        order.setFullName(orderDTO.getFullName());
        order.setOrderType(orderDTO.getOrderType());
        order.setTotalPrice(orderDTO.getTotalPrice());
        order.setDiscountAmount(orderDTO.getDiscountAmount());
        order.setDeliveryAddress(orderDTO.getDeliveryAddress());
        order.setDeliveryPhone(orderDTO.getDeliveryPhone());
        order.setStatus(orderDTO.getStatus());
        order.setTable(orderDTO.getTable());
        order.setNote(orderDTO.getNote());
        order.setShippingTime(orderDTO.getShippingTime());
        order.setPaymentMethod(orderDTO.getPaymentMethod());
        order.setEmail(orderDTO.getEmail());


        orderRepository.save(order);
        Membership existingMembership = membershipRepository.getMembershipByPhone(order.getPhoneOrder()).orElse(null);
        if(existingMembership != null){
            if(Order.STATUS_COMPLETED.equals(order.getStatus())){
                Float newTotalSpent = orderRepository.sumTotalAmount(
                        order.getPhoneOrder());
                String newTier = getTierByTotalSpent(newTotalSpent);
                Float newDiscount = getDiscountByTier(newTier);
                MembershipDTO membershipDTO = MembershipDTO.builder()
                        .phone(order.getPhoneOrder())
                        .totalSpent(newTotalSpent)
                        .membershipTier(newTier)
                        .discountRate(newDiscount)
                        .build();
                membershipService.updateMembership(existingMembership.getId(),membershipDTO);
                existingMembership.setTotalSpent(newTotalSpent);
            }
        }
        return order;
    }

    @Override
    public void deleteOrder(long id) {
        Order order = orderRepository.findById(id).orElse(null);
        //khong xoa cung => xoa mem
        if(order !=null){
            order.setStatus(Order.STATUS_CANCELLED);
            order.setActive(false);
            orderRepository.save(order);
        }
    }

    @Override
    public List<Order> findByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public Page<Order> getOrdersByKeyword(String keyword, Pageable pageable) {
        return orderRepository.findByKeyword(keyword,pageable);
    }

    @Override
    public Order updateTierInMembership(String phone) {
        return null;
    }

    @Override
    public List<Order> getUserOrderCanceled(Long userId) {
        return orderRepository.getUserOrderCanceled(userId);
    }


    public static Float getDiscountByPriceOrder(Float price) {
        if (price == null || price < Order.VALUE_1000000) {
            return Order.DISCOUNT_BRONZE;
        }
        if (price >= Order.VALUE_10000000) {
            return Order.DISCOUNT_PLATINUM;
        }
        if (price >=Order. VALUE_3000000) {
            return Order.DISCOUNT_GOLD;
        }
        if (price >= Order.VALUE_1000000) {
            return Order.DISCOUNT_SILVER;
        }
        return Order.DISCOUNT_BRONZE;
    }
    public static String getTierByTotalSpent(Float spent) {
        if (spent == null || spent < Order.VALUE_1000000) {
            return Membership.MEMBER_BRONZE;
        }
        if (spent >= Order.VALUE_10000000) {
            return Membership.MEMBER_PLATINUM;
        }
        if (spent >=Order. VALUE_3000000) {
            return Membership.MEMBER_GOLD;
        }
        if (spent >= Order.VALUE_1000000) {
            return Membership.MEMBER_SILVER;
        }
        return Membership.MEMBER_BRONZE;
    }

    public static Float getDiscountByTier(String tier) {
        if(tier != null){
            if(tier.equals(Membership.MEMBER_BRONZE)){
                return Membership.DISCOUNT_BRONZE;
            }
            if(tier.equals(Membership.MEMBER_SILVER)){
                return Membership.DISCOUNT_SILVER;
            }
            if(tier.equals(Membership.MEMBER_GOLD)){
                return Membership.DISCOUNT_GOLD;
            }
            if(tier.equals(Membership.MEMBER_PLATINUM)){
                return Membership.DISCOUNT_PLATINUM;
            }
        }
        return null;
    }

}
