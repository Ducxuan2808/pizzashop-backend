package com.example.pizzashop.converter;


import com.example.pizzashop.models.Pizza;
import org.springframework.kafka.support.converter.JsonMessageConverter;
import org.springframework.kafka.support.mapping.DefaultJackson2JavaTypeMapper;
import org.springframework.kafka.support.mapping.Jackson2JavaTypeMapper;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class PizzaMessageConverter extends JsonMessageConverter {

    public  PizzaMessageConverter() {
        super();
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setTypePrecedence(Jackson2JavaTypeMapper.TypePrecedence.TYPE_ID);
        typeMapper.addTrustedPackages("com.example.pizzashop");
        typeMapper.setIdClassMapping(Collections.singletonMap("pizza", Pizza.class));
        this.setTypeMapper(typeMapper);
    }
}
