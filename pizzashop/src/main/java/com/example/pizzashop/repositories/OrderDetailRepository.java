package com.example.pizzashop.repositories;

import com.example.pizzashop.models.OrderDetail;
import com.example.pizzashop.models.Pizza;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail,Long> {
    List<OrderDetail> findByOrderId(Long orderId);

    @Query("SELECT SUM(o.quantity) FROM OrderDetail o WHERE o.pizza.id = :pizzaId")
    Integer getCountSoldByPizzaId(@Param("pizzaId") Long pizzaId);


}
