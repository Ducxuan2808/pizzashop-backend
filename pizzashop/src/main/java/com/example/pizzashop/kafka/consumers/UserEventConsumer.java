package com.example.pizzashop.kafka.consumers;

import com.example.pizzashop.kafka.KafkaTopics;
import com.example.pizzashop.kafka.events.UserEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UserEventConsumer {
    private static final Logger logger = LoggerFactory.getLogger(UserEventConsumer.class);

    @KafkaListener(topics = KafkaTopics.USER_EVENTS, groupId = "pizzashop-user-group")
    public void consume(UserEvent event) {
        logger.info("Nhận {}: {}", event.getEventType(), event);
        // TODO: xử lý bất đồng bộ (gửi email/thông báo, thống kê, ...)
    }
}
