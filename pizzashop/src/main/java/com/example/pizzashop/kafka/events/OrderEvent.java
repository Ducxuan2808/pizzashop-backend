package com.example.pizzashop.kafka.events;

import com.example.pizzashop.models.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {
    public static final String CREATED = "ORDER_CREATED";
    public static final String UPDATED = "ORDER_UPDATED";
    public static final String STATUS_CHANGED = "ORDER_STATUS_CHANGED";
    public static final String CANCELLED = "ORDER_CANCELLED";

    private String eventType;
    private Long orderId;
    private Long userId;
    private String fullName;
    private String email;
    private String phoneOrder;
    private String orderType;
    private String status;
    private String previousStatus;
    private Float totalPrice;
    private Float discountAmount;
    private String paymentMethod;
    private Integer itemCount;
    private long occurredAt;

    public static OrderEvent of(String eventType, Order order, String previousStatus, Integer itemCount) {
        return OrderEvent.builder()
                .eventType(eventType)
                .orderId(order.getId())
                .userId(order.getUser() != null ? order.getUser().getId() : null)
                .fullName(order.getFullName())
                .email(order.getEmail())
                .phoneOrder(order.getPhoneOrder())
                .orderType(order.getOrderType())
                .status(order.getStatus())
                .previousStatus(previousStatus)
                .totalPrice(order.getTotalPrice())
                .discountAmount(order.getDiscountAmount())
                .paymentMethod(order.getPaymentMethod())
                .itemCount(itemCount)
                .occurredAt(System.currentTimeMillis())
                .build();
    }
}
