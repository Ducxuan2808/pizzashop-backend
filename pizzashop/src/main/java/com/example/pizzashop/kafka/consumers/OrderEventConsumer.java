package com.example.pizzashop.kafka.consumers;

import com.example.pizzashop.kafka.KafkaTopics;
import com.example.pizzashop.kafka.events.OrderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventConsumer {
    private static final Logger logger = LoggerFactory.getLogger(OrderEventConsumer.class);

    @KafkaListener(topics = KafkaTopics.ORDER_EVENTS, groupId = "pizzashop-order-group")
    public void consume(OrderEvent event) {
        logger.info("Nhận {}: {}", event.getEventType(), event);
        // TODO: xử lý bất đồng bộ (gửi email/thông báo, thống kê, ...)
    }
}
