package com.example.pizzashop.kafka;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Gửi event lên Kafka.
 * - Nếu đang trong transaction: chỉ gửi sau khi commit thành công (tránh gửi event cho dữ liệu bị rollback).
 * - Lỗi Kafka chỉ được log lại, không làm hỏng request nghiệp vụ.
 */
@Component
@RequiredArgsConstructor
public class KafkaEventPublisher {
    private static final Logger logger = LoggerFactory.getLogger(KafkaEventPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(String topic, Object key, Object event) {
        String messageKey = key != null ? String.valueOf(key) : null;
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    send(topic, messageKey, event);
                }
            });
        } else {
            send(topic, messageKey, event);
        }
    }

    private void send(String topic, String key, Object event) {
        try {
            kafkaTemplate.send(topic, key, event).whenComplete((result, ex) -> {
                if (ex != null) {
                    logger.error("Gửi event tới topic {} thất bại (key={}): {}", topic, key, ex.getMessage());
                } else {
                    logger.info("Đã gửi event tới {} [partition={}, offset={}]: {}", topic,
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset(), event);
                }
            });
        } catch (Exception e) {
            logger.error("Không thể gửi event tới topic {}: {}", topic, e.getMessage());
        }
    }
}
