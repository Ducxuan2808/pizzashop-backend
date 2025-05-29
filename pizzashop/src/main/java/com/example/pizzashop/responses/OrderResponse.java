package com.example.pizzashop.responses;

import com.example.pizzashop.dtos.OrderDetailDTO;
import com.example.pizzashop.models.Order;
import com.example.pizzashop.models.OrderDetail;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse {
    private Long id;
    private Long userId;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("order_type")
    private String orderType;

    @JsonProperty("email")
    private String email;

    @JsonProperty("phone_order")
    private String phoneOrder;

    @JsonProperty("delivery_address")
    private String deliveryAddress;

    @JsonProperty("delivery_phone")
    private String deliveryPhone;

    @JsonProperty("total_price")
    private Float totalPrice;

    @JsonProperty("discount_amount")
    private Float discountAmount;

    @JsonProperty("status")
    private String status;

    @JsonProperty("order_time")
    private Date orderTime;

    @JsonProperty("order_details")
    private List<OrderDetail> orderDetails;

    @JsonProperty("table_number")
    private Integer table;

    @JsonProperty("shipping_time")
    private java.util.Date shippingTime;

    @JsonProperty("note")
    private String note;

    @JsonProperty("payment_method")
    private String paymentMethod;

    public static OrderResponse fromOrder(Order order){
        OrderResponse orderResponse = OrderResponse
                .builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .fullName(order.getFullName())
                .email(order.getEmail())
                .phoneOrder(order.getPhoneOrder())
                .orderType(order.getOrderType())
                .deliveryAddress(order.getDeliveryAddress())
                .deliveryPhone(order.getDeliveryPhone())
                .note(order.getNote())
                .table(order.getTable())
                .orderTime(order.getOrderTime())
                .shippingTime(order.getShippingTime())
                .paymentMethod(order.getPaymentMethod())
                .status(order.getStatus())
                .totalPrice(order.getTotalPrice())
                .discountAmount(order.getDiscountAmount())
                .orderDetails(order.getOrderDetails())
                .build();
        return  orderResponse;
    }
}
