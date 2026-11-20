package com.example.pizzashop.kafka;

/**
 * Tên các topic Kafka dùng trong hệ thống.
 */
public final class KafkaTopics {
    public static final String ORDER_EVENTS = "pizzashop.order-events";
    public static final String USER_EVENTS = "pizzashop.user-events";
    public static final String PIZZA_EVENTS = "pizzashop.pizza-events";
    public static final String TABLE_BOOKING_EVENTS = "pizzashop.table-booking-events";

    private KafkaTopics() {
    }
}
