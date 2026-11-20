package com.example.pizzashop.kafka.consumers;

import com.example.pizzashop.kafka.KafkaTopics;
import com.example.pizzashop.kafka.events.PizzaEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PizzaEventConsumer {
    private static final Logger logger = LoggerFactory.getLogger(PizzaEventConsumer.class);

    @KafkaListener(topics = KafkaTopics.PIZZA_EVENTS, groupId = "pizzashop-pizza-group")
    public void consume(PizzaEvent event) {
        logger.info("Nhận {}: {}", event.getEventType(), event);
        // TODO: xử lý bất đồng bộ (gửi email/thông báo, thống kê, ...)
    }
}
