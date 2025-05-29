package com.example.pizzashop.responses;

import lombok.*;

import java.util.List;
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PizzaListResponse {
    private List<PizzaResponse> pizzas;
    private int totalPages;
}
