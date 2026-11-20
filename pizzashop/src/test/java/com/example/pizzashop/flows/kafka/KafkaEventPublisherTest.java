package com.example.pizzashop.flows.kafka;

import com.example.pizzashop.kafka.KafkaEventPublisher;
import com.example.pizzashop.kafka.KafkaTopics;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Kafka - gửi event")
class KafkaEventPublisherTest {
    @Mock KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks KafkaEventPublisher publisher;

    @AfterEach
    void clearTransaction() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test
    @DisplayName("Không có transaction: gửi ngay, key là id dạng chuỗi")
    void sendsImmediatelyWithoutTransaction() {
        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        when(kafkaTemplate.send(KafkaTopics.ORDER_EVENTS, "10", "payload")).thenReturn(future);

        publisher.publish(KafkaTopics.ORDER_EVENTS, 10L, "payload");

        verify(kafkaTemplate).send(KafkaTopics.ORDER_EVENTS, "10", "payload");
    }

    @Test
    @DisplayName("Trong transaction: chỉ gửi sau khi commit")
    void sendsAfterCommit() {
        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(new CompletableFuture<>());
        TransactionSynchronizationManager.initSynchronization();

        publisher.publish(KafkaTopics.USER_EVENTS, 1L, "payload");
        verify(kafkaTemplate, never()).send(anyString(), anyString(), any());

        TransactionSynchronizationManager.getSynchronizations().forEach(TransactionSynchronization::afterCommit);
        verify(kafkaTemplate).send(KafkaTopics.USER_EVENTS, "1", "payload");
    }

    @Test
    @DisplayName("Transaction rollback: không gửi event")
    void doesNotSendOnRollback() {
        TransactionSynchronizationManager.initSynchronization();

        publisher.publish(KafkaTopics.USER_EVENTS, 1L, "payload");
        TransactionSynchronizationManager.getSynchronizations()
                .forEach(s -> s.afterCompletion(TransactionSynchronization.STATUS_ROLLED_BACK));

        verify(kafkaTemplate, never()).send(anyString(), anyString(), any());
    }

    @Test
    @DisplayName("Kafka lỗi: không ném exception ra nghiệp vụ")
    void swallowsKafkaErrors() {
        when(kafkaTemplate.send(anyString(), anyString(), any()))
                .thenThrow(new RuntimeException("broker down"));

        assertThatCode(() -> publisher.publish(KafkaTopics.PIZZA_EVENTS, 1L, "payload"))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Gửi thất bại bất đồng bộ: chỉ log, không ném exception")
    void handlesAsyncFailure() {
        CompletableFuture<SendResult<String, Object>> failed = new CompletableFuture<>();
        failed.completeExceptionally(new RuntimeException("timeout"));
        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(failed);

        assertThatCode(() -> publisher.publish(KafkaTopics.PIZZA_EVENTS, 1L, "payload"))
                .doesNotThrowAnyException();
    }
}
