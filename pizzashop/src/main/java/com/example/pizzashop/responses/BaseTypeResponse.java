package com.example.pizzashop.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import com.example.pizzashop.models.*;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BaseTypeResponse {

    @JsonProperty("base_type")
    private BaseType baseType;

    @JsonProperty("price")
    private Float price;
}
