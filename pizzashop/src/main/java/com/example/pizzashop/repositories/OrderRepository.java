package com.example.pizzashop.repositories;

import com.example.pizzashop.models.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Long> {
    @Query("SELECT o FROM Order o " +
            "WHERE o.active = true AND o.user.id = :userId")
    List<Order> findByUserId(Long userId);

    @Query("""
    SELECT o FROM Order o
    WHERE 
        :keyword IS NULL OR TRIM(:keyword) = '' OR
        LOWER(o.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
        LOWER(o.deliveryAddress) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
        LOWER(o.note) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
        LOWER(o.deliveryPhone) LIKE LOWER(CONCAT('%', :keyword, '%'))
    
""")
    Page<Order> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("""
    SELECT SUM(o.totalPrice)
    FROM Order o
    JOIN o.user u
    WHERE (o.phoneOrder = :phone)
      AND o.status = 'Completed' AND o.active = true
""")
    Float sumTotalAmount(@Param("phone") String phone);

    @Query("SELECT o FROM Order o " +
            "WHERE o.active = false AND o.user.id = :userId")
    List<Order> getUserOrderCanceled(Long userId);
}
