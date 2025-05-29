package com.example.pizzashop.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PizzaDTO {
    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 200 , message = "Name must be between 3 and 200 characters")
    private String name;

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 200 , message = "Title must be between 3 and 200 characters")
    private String title;

    @JsonProperty("base_price")
    @Min(value = 0 , message = "Giá >=0")
    @Max(value = 10000000, message = "Giá <=10000000")
    private Float basePrice;

    private String thumbnail;
    private String description;



}
