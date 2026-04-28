package com.example.pizzashop.models;

import com.example.pizzashop.services.IPizzaRedisService;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@AllArgsConstructor
public class PizzaListener {

    private final IPizzaRedisService pizzaRedisService;
    private static final Logger logger = LoggerFactory.getLogger(PizzaListener.class);

    @PrePersist
    public void prePersist(Pizza pizza) {
        logger.info("prePersist");
    }

    @PostPersist //save = persis
    public void postPersist(Pizza pizza) {
        // Update Redis cache
        logger.info("postPersist");
        pizzaRedisService.clear();
    }

    @PreUpdate
    public void preUpdate(Pizza pizza) {
        //ApplicationEventPublisher.instance().publishEvent(event);
        logger.info("preUpdate");
    }

    @PostUpdate
    public void postUpdate(Pizza pizza) {
        // Update Redis cache
        logger.info("postUpdate");
        pizzaRedisService.clear();
    }

    @PreRemove
    public void preRemove(Pizza pizza) {
        //ApplicationEventPublisher.instance().publishEvent(event);
        logger.info("preRemove");
    }

    @PostRemove
    public void postRemove(Pizza pizza) {
        // Update Redis cache
        logger.info("postRemove");
        pizzaRedisService.clear();
    }

}
