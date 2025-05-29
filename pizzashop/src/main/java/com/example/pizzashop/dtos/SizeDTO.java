package com.example.pizzashop.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SizeDTO {

    @JsonProperty("size_name")
    private String sizeName;

    @JsonProperty("price_multiplier")
    private Float priceMultiplier;
}
