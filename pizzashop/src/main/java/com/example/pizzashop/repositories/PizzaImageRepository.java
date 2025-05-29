package com.example.pizzashop.repositories;

import com.example.pizzashop.models.BaseType;
import com.example.pizzashop.models.PizzaImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PizzaImageRepository extends JpaRepository<PizzaImage,Long> {
    List<PizzaImage> findByPizzaId(Long pizzaId);
}
