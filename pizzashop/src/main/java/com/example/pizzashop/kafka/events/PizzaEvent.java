package com.example.pizzashop.kafka.events;

import com.example.pizzashop.models.Pizza;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PizzaEvent {
    public static final String CREATED = "PIZZA_CREATED";
    public static final String UPDATED = "PIZZA_UPDATED";
    public static final String DELETED = "PIZZA_DELETED";

    private String eventType;
    private Long pizzaId;
    private String name;
    private String title;
    private Float basePrice;
    private String thumbnail;
    private Boolean active;
    private long occurredAt;

    public static PizzaEvent of(String eventType, Pizza pizza) {
        return PizzaEvent.builder()
                .eventType(eventType)
                .pizzaId(pizza.getId())
                .name(pizza.getName())
                .title(pizza.getTitle())
                .basePrice(pizza.getBasePrice())
                .thumbnail(pizza.getThumbnail())
                .active(pizza.getActive())
                .occurredAt(System.currentTimeMillis())
                .build();
    }
}
