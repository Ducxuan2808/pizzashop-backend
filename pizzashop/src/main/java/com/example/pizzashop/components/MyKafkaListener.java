package com.example.pizzashop.components;

import com.example.pizzashop.models.Pizza;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@KafkaListener(id = "groupA", topics = { "get-all-pizzas", "insert-a-pizza" })
public class MyKafkaListener {
    @KafkaHandler
    public void listenPizza(Pizza pizza) {
        System.out.println("Received: " + pizza);
    }

    @KafkaHandler(isDefault = true)
    public void unknown(Object object) {
        System.out.println("Received unknown: " + object);
    }
    @KafkaHandler
    public void listenListOfPizzas(List<Pizza> pizzas) {
        System.out.println("Received: " + pizzas);
    }
}