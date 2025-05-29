package com.example.pizzashop.responses;

import com.example.pizzashop.models.Size;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SizeResponse {
    @JsonProperty("size")
    private Size size;
}
