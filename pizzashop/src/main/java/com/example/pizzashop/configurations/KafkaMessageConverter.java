package com.example.pizzashop.configurations;

import com.example.pizzashop.kafka.events.OrderEvent;
import com.example.pizzashop.kafka.events.PizzaEvent;
import com.example.pizzashop.kafka.events.TableBookingEvent;
import com.example.pizzashop.kafka.events.UserEvent;
import org.springframework.kafka.support.converter.JsonMessageConverter;
import org.springframework.kafka.support.mapping.DefaultJackson2JavaTypeMapper;
import org.springframework.kafka.support.mapping.Jackson2JavaTypeMapper;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Chuyển JSON từ Kafka thành event object dựa vào header __TypeId__.
 * Các type id phải khớp với spring.json.type.mapping của producer trong application.yml.
 */
@Component
public class KafkaMessageConverter extends JsonMessageConverter {

    public KafkaMessageConverter() {
        super();
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setTypePrecedence(Jackson2JavaTypeMapper.TypePrecedence.TYPE_ID);
        typeMapper.addTrustedPackages("com.example.pizzashop");
        typeMapper.setIdClassMapping(Map.of(
                "orderEvent", OrderEvent.class,
                "userEvent", UserEvent.class,
                "pizzaEvent", PizzaEvent.class,
                "tableBookingEvent", TableBookingEvent.class
        ));
        this.setTypeMapper(typeMapper);
    }
}
