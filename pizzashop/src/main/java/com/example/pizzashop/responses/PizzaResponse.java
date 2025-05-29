package com.example.pizzashop.responses;

import com.example.pizzashop.models.Pizza;
import com.example.pizzashop.models.PizzaImage;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PizzaResponse extends  BaseResponse{
    private Long id;

    private String name;

    private String title;

    @JsonProperty("base_price")
    private Float basePrice;

    private String thumbnail;
    private String description;

    @JsonProperty("pizza_images")
    private List<PizzaImage> pizzaImages = new ArrayList<>();

    @JsonProperty("is_active")
    private Boolean active;//thuộc về admin

    public static  PizzaResponse fromPizza(Pizza pizza){
        PizzaResponse pizzaResponse = PizzaResponse.builder()
                .id(pizza.getId())
                .name(pizza.getName())
                .title(pizza.getTitle())
                .basePrice(pizza.getBasePrice())
                .thumbnail(pizza.getThumbnail())
                .description(pizza.getDescription())
                .pizzaImages(pizza.getPizzaImages())
                .active(pizza.getActive())
                .build();
        pizzaResponse.setCreatedAt(pizza.getCreatedAt());
        pizzaResponse.setUpdatedAt(pizza.getUpdatedAt());
        return pizzaResponse;
    }
}
