package com.example.pizzashop.repositories;

import com.example.pizzashop.models.Pizza;
import com.example.pizzashop.models.TableBooking;
import com.example.pizzashop.responses.TableBookingResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TableBookingRepository extends JpaRepository<TableBooking,Long> {
    @Query("SELECT t FROM TableBooking t WHERE t.active = true AND " +
            "(:keyword IS NULL OR :keyword = '' OR " +
            "LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(t.phone) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<TableBooking> getAllTableBookings(@Param("keyword") String keyword);

}
