package com.example.pizzashop.repositories;

import com.example.pizzashop.models.BaseType;
import com.example.pizzashop.models.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BaseTypeRepository extends JpaRepository<BaseType,Long> {

    @Query("SELECT t FROM BaseType t " +
            "WHERE t.active = true")
    List<BaseType> getAll();
    BaseType getBaseTypeById(Long id);

    Optional<BaseType> findByBaseName(String name) throws Exception;
}
