package com.example.pizzashop.services;

import com.example.pizzashop.responses.PizzaListResponse;
import com.example.pizzashop.responses.PizzaResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface IPizzaRedisService {
    void clear();
    List<PizzaResponse> getPizzas(String sortBy,
                                  Float minPrice,
                                  Float maxPrice,
                                  String keyword,
                                  PageRequest pageRequest) throws JsonProcessingException;

    void saveAllListPizza(List<PizzaResponse> pizzaResponses,
                          String sortBy,
                          Float minPrice,
                          Float maxPrice,
                          String keyword,
                          PageRequest pageRequest) throws JsonProcessingException;
}
