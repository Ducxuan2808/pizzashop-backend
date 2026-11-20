package com.example.pizzashop.kafka.consumers;

import com.example.pizzashop.kafka.KafkaTopics;
import com.example.pizzashop.kafka.events.TableBookingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TableBookingEventConsumer {
    private static final Logger logger = LoggerFactory.getLogger(TableBookingEventConsumer.class);

    @KafkaListener(topics = KafkaTopics.TABLE_BOOKING_EVENTS, groupId = "pizzashop-table-booking-group")
    public void consume(TableBookingEvent event) {
        logger.info("Nhận {}: {}", event.getEventType(), event);
        // TODO: xử lý bất đồng bộ (gửi email/thông báo, thống kê, ...)
    }
}
