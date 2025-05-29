package com.example.pizzashop.repositories;

import com.example.pizzashop.models.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PizzaRepository extends JpaRepository<Pizza,Long> {
    boolean existsByName(String name);

    Page<Pizza> findAll(Pageable pageable);//phan trang

    @Query("SELECT p FROM Pizza p WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:minPrice IS NULL OR p.basePrice >= :minPrice) " +
            "AND (:maxPrice IS NULL OR p.basePrice <= :maxPrice) " +
            "AND p.active = true " +
            "ORDER BY " +
            "CASE WHEN :sortBy = 'price_asc' THEN p.basePrice END ASC, " +
            "CASE WHEN :sortBy = 'price_desc' THEN p.basePrice END DESC, " +
            "CASE WHEN :sortBy = 'name_asc' THEN p.name END ASC, " +
            "CASE WHEN :sortBy = 'name_desc' THEN p.name END DESC, " +
            "CASE WHEN :sortBy = 'newest' THEN p.createdAt END DESC")
    Page<Pizza> searchPizzas(
            @Param("sortBy") String sortBy,
            @Param("minPrice") Float minPrice,
            @Param("maxPrice") Float maxPrice,
            @Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Pizza  p LEFT JOIN FETCH p.pizzaImages WHERE p.id = :pizzaId")
    Optional<Pizza> getDetailPizza(@Param("pizzaId") Long pizzaId);

    @Query("SELECT p FROM Pizza p WHERE p.id IN :pizzaIds")
    List<Pizza> findPizzasByIds(@Param("pizzaIds") List<Long> pizzaIds);

    @Query("SELECT p " +
            "FROM Pizza p " +
            "JOIN OrderDetail od ON p.id = od.pizza.id " +
            "GROUP BY p.id " +
            "ORDER BY SUM(od.quantity) DESC")
    Page<Pizza> bestSellPizzas(Pageable pageable);

    @Query("SELECT p " +
            "FROM Pizza p " +
            "JOIN OrderDetail od ON p.id = od.pizza.id " +
            "GROUP BY p.id " +
            "ORDER BY SUM(od.quantity) ASC")
    Page<Pizza> worstSellPizzas(Pageable pageable);

    @Query("SELECT p FROM Pizza p WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:minPrice IS NULL OR p.basePrice >= :minPrice) " +
            "AND (:maxPrice IS NULL OR p.basePrice <= :maxPrice) " +
            "AND p.active = true " +
            "ORDER BY " +
            "CASE WHEN :sortBy = 'price_asc' THEN p.basePrice END ASC, " +
            "CASE WHEN :sortBy = 'price_desc' THEN p.basePrice END DESC, " +
            "CASE WHEN :sortBy = 'name_asc' THEN p.name END ASC, " +
            "CASE WHEN :sortBy = 'name_desc' THEN p.name END DESC, " +
            "CASE WHEN :sortBy = 'newest' THEN p.createdAt END DESC")
    List<Pizza> revenueStatistics(
            @Param("keyword") String keyword, Pageable pageable);

}
