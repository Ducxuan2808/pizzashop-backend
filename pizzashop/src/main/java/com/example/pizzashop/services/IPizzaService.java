package com.example.pizzashop.services;

import com.example.pizzashop.dtos.PizzaDTO;
import com.example.pizzashop.dtos.PizzaImageDTO;
import com.example.pizzashop.models.Pizza;
import com.example.pizzashop.models.PizzaImage;
import com.example.pizzashop.responses.PizzaResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IPizzaService {
    Pizza createPizza(PizzaDTO pizzaDTO) throws Exception;
    Pizza getPizzaById(long pizzaID) throws Exception;
    Page<PizzaResponse> getAllPizzas(String sortBy, Float minPrice, Float maxPrice, String keyword, PageRequest pageRequest);
    Pizza updatePizza(long id, PizzaDTO pizzaDTO) throws Exception;
    void deletePizza(long id) throws Exception;
    boolean existByName(String name);
    PizzaImage createPizzaImage(Long pizzaId, PizzaImageDTO pizzaImageDTO) throws Exception;
    List<Pizza> findPizzasByIds(List<Long> pizzaIds );
    Page<PizzaResponse> bestSellPizzas(PageRequest pageRequest);
    Page<PizzaResponse> worstSellPizzas(PageRequest pageRequest);
    Integer getCountSoldByPizzaId(long pizzaId);
}
