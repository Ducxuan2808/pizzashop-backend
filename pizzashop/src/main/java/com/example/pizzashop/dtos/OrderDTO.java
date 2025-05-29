package com.example.pizzashop.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
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
public class OrderDTO {
    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("email")
    private String email;

    @JsonProperty("phone_order")
    private String phoneOrder;

    @JsonProperty("order_type")
    @NotBlank(message = "Order type is required")
    private String orderType;

    @JsonProperty("delivery_address")
    private String deliveryAddress;

    @JsonProperty("delivery_phone")
    @NotBlank(message = "Delivery phone is required")
    private String deliveryPhone;

    @JsonProperty("total_price")
    @Min(value = 0, message = "Total price must be at least 0")
    private Float totalPrice;

    @JsonProperty("discount_amount")
    private Float discountAmount;

    @JsonProperty("status")
    private String status;

    @JsonProperty("order_time")
    private Date orderTime;

//    @JsonProperty("order_details")
//    @NotEmpty(message = "Order details cannot be empty")
//    private List<OrderDetailDTO> orderDetails;

    @JsonProperty("cart_items")
    private List<CartItemDTO> cartItems;

    @JsonProperty("table_number")
    private Integer table;

    @JsonProperty("shipping_time")
    private java.util.Date shippingTime;

    @JsonProperty("note")
    private String note;

    @JsonProperty("payment_method")
    private String paymentMethod;
}