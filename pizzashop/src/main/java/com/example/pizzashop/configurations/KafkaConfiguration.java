package com.example.pizzashop.configurations;

import com.example.pizzashop.kafka.KafkaTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConfiguration {
    // Retry 2 lần (cách nhau 1s), sau đó đẩy message lỗi sang topic <topic>.DLT
    @Bean
    public CommonErrorHandler errorHandler(KafkaOperations<Object, Object> template) {
        return new DefaultErrorHandler(
                new DeadLetterPublishingRecoverer(template), new FixedBackOff(1000L, 2));
    }

    @Bean
    public NewTopic orderEventsTopic() {
        return TopicBuilder.name(KafkaTopics.ORDER_EVENTS).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic userEventsTopic() {
        return TopicBuilder.name(KafkaTopics.USER_EVENTS).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic pizzaEventsTopic() {
        return TopicBuilder.name(KafkaTopics.PIZZA_EVENTS).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic tableBookingEventsTopic() {
        return TopicBuilder.name(KafkaTopics.TABLE_BOOKING_EVENTS).partitions(1).replicas(1).build();
    }
}
