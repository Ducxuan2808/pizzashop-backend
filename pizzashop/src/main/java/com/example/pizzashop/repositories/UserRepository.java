package com.example.pizzashop.repositories;

import com.example.pizzashop.models.Pizza;
import com.example.pizzashop.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    boolean existsByPhone(String phone);
    Optional<User> findByPhone (String phone);

    @Query("SELECT u FROM User u WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR " +
            "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.phone) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND u.active = true")
    Page<User> getAllUsers(
            @Param("keyword") String keyword, Pageable pageable);
}
