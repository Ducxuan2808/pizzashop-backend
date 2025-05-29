package com.example.pizzashop.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import lombok.*;
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemDTO {
    @JsonProperty("pizza_id")
    @Min(value = 1, message = "Pizza ID must be greater than 0")
    private Long pizzaId;

    @JsonProperty("size_id")
    @Min(value = 1, message = "Size ID must be greater than 0")
    private Long sizeId;

    @JsonProperty("base_id")
    @Min(value = 1, message = "Base ID must be greater than 0")
    private Long baseId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @JsonProperty("price")
    @Min(value = 0, message = "Price must be at least 0")
    private Float price;

    @JsonProperty("note")
    private String note;
}
