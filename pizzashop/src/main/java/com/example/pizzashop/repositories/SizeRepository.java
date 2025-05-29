package com.example.pizzashop.repositories;

import com.example.pizzashop.models.BaseType;
import com.example.pizzashop.models.Size;
import com.example.pizzashop.responses.PizzaResponse;
import com.example.pizzashop.responses.SizeResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SizeRepository extends JpaRepository<Size,Long> {
    @Query("SELECT s FROM Size s " +
            "WHERE s.active = true")
    List<Size> getAll();

    Size getSizeById(Long id);
    Optional<Size> findBySizeName(String name) throws Exception;

}
