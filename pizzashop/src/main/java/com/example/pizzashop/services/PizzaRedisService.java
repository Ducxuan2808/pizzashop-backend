package com.example.pizzashop.services;

import com.example.pizzashop.responses.PizzaListResponse;
import com.example.pizzashop.responses.PizzaResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PizzaRedisService implements IPizzaRedisService{

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper redisObjectMapper;

    private String getKeyFrom(String sortBy,
                              Float minPrice,
                              Float maxPrice,
                              String keyword,
                              PageRequest pageRequest) {
        int pageNumber = pageRequest.getPageNumber();
        int pageSize = pageRequest.getPageSize();
        Sort sort = pageRequest.getSort();
        String sortDirection = sort.getOrderFor("id")
                .getDirection() == Sort.Direction.ASC ? "asc": "desc";
        String key = String.format("all_products:%s:%s:%s:%s:%d:%d:%s",
                sortBy != null ? sortBy : "",
                minPrice != null ? minPrice : 0,
                maxPrice != null ? maxPrice : 0,
                keyword != null ? keyword : "",
                pageNumber,
                pageSize,
                sortDirection);
        return key;
    }
    @Override
    public void clear() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Override
    public List<PizzaResponse> getPizzas(String sortBy, Float minPrice, Float maxPrice, String keyword, PageRequest pageRequest) throws JsonProcessingException {
        String key = this.getKeyFrom(sortBy, minPrice, maxPrice, keyword, pageRequest);
        String json = (String) redisTemplate.opsForValue().get(key);
        List<PizzaResponse> pizzaResponses =
                json != null ?
                        redisObjectMapper.readValue(json, new TypeReference<List<PizzaResponse>>() {})
                        : null;
        return pizzaResponses;
    }

    @Override
    public void saveAllListPizza(List<PizzaResponse> pizzaResponses, String sortBy, Float minPrice, Float maxPrice, String keyword, PageRequest pageRequest) throws JsonProcessingException {
        String key = this.getKeyFrom(sortBy, minPrice, maxPrice, keyword, pageRequest);
        String json = redisObjectMapper.writeValueAsString(pizzaResponses);
        redisTemplate.opsForValue().set(key, json);
    }
}
