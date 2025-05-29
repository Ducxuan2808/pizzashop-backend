package com.example.pizzashop.repositories;

import com.example.pizzashop.models.OrderDetail;
import com.example.pizzashop.models.Pizza;
import com.example.pizzashop.models.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review,Long> {
    @Query("SELECT r FROM Review r " +
            "WHERE r.orderDetail.pizza.id = :pizzaId AND r.active = true")
    List<Review> getByPizzaId(@Param("pizzaId") Long pizzaId) throws Exception;

    @Query("SELECT r FROM Review r " +
            "WHERE r.orderDetail.order.id = :orderId AND r.active = true")
    List<Review> getByOrderId(@Param("orderId") Long orderId) throws Exception;

    @Query("SELECT r FROM Review r " +
            "WHERE r.orderDetail.id = :orderDetailId AND r.active = true")
    Optional<Review> getByOrderDetailId(@Param("orderDetailId") Long orderDetailId);

    boolean existsByOrderDetail(OrderDetail orderDetailId);
}
