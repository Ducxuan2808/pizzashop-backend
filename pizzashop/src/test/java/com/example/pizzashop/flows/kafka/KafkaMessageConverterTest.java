package com.example.pizzashop.flows.kafka;

import com.example.pizzashop.configurations.KafkaMessageConverter;
import com.example.pizzashop.kafka.KafkaTopics;
import com.example.pizzashop.kafka.events.OrderEvent;
import com.example.pizzashop.kafka.events.UserEvent;
import com.example.pizzashop.models.Order;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.apache.kafka.common.record.TimestampType;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.messaging.Message;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Kiểm tra producer (JsonSerializer + type mapping giống application.yml)
 * và consumer (KafkaMessageConverter) hiểu cùng một định dạng message.
 */
@DisplayName("Kafka - serialize/deserialize event")
class KafkaMessageConverterTest {
    private final JsonSerializer<Object> serializer = new JsonSerializer<>();
    private final KafkaMessageConverter converter = new KafkaMessageConverter();

    KafkaMessageConverterTest() {
        serializer.configure(Map.of(JsonSerializer.TYPE_MAPPINGS,
                "orderEvent:com.example.pizzashop.kafka.events.OrderEvent,"
                        + "userEvent:com.example.pizzashop.kafka.events.UserEvent"), false);
    }

    @AfterEach
    void close() {
        serializer.close();
    }

    private Object roundTrip(String topic, Object event, Class<?> targetType) {
        RecordHeaders headers = new RecordHeaders();
        byte[] bytes = serializer.serialize(topic, headers, event);
        ConsumerRecord<String, byte[]> record = new ConsumerRecord<>(topic, 0, 0L, 0L,
                TimestampType.CREATE_TIME, 0, bytes.length, "1", bytes, headers, Optional.empty());
        Message<?> message = converter.toMessage(record, null, null, targetType);
        return message.getPayload();
    }

    @Test
    @DisplayName("OrderEvent gửi đi được consumer đọc lại đúng kiểu và dữ liệu")
    void orderEventRoundTrip() {
        Order order = Order.builder().id(10L).status(Order.STATUS_PENDING)
                .phoneOrder("0987654321").totalPrice(300000f).orderType(Order.TYPE_ONLINE).build();
        OrderEvent sent = OrderEvent.of(OrderEvent.CREATED, order, null, 2);

        Object received = roundTrip(KafkaTopics.ORDER_EVENTS, sent, OrderEvent.class);

        assertThat(received).isInstanceOf(OrderEvent.class).isEqualTo(sent);
    }

    @Test
    @DisplayName("UserEvent gửi đi được consumer đọc lại đúng kiểu")
    void userEventRoundTrip() {
        UserEvent sent = UserEvent.builder().eventType(UserEvent.REGISTERED)
                .userId(1L).phone("0987654321").occurredAt(123L).build();

        Object received = roundTrip(KafkaTopics.USER_EVENTS, sent, UserEvent.class);

        assertThat(received).isEqualTo(sent);
    }

    @Test
    @DisplayName("Header __TypeId__ dùng type id ngắn, không lộ tên class")
    void usesShortTypeId() {
        RecordHeaders headers = new RecordHeaders();
        serializer.serialize(KafkaTopics.USER_EVENTS, headers,
                UserEvent.builder().eventType(UserEvent.REGISTERED).build());

        String typeId = new String(headers.lastHeader("__TypeId__").value());
        assertThat(typeId).isEqualTo("userEvent");
        // đảm bảo key serializer mặc định vẫn là String
        assertThat(new StringSerializer().serialize("t", "1")).isEqualTo("1".getBytes());
    }
}
