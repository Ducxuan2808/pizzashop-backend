package com.example.pizzashop.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BaseTypeDTO {
    @JsonProperty("base_name")
    private String baseName;

    @JsonProperty("price")
    private Float price;
}
