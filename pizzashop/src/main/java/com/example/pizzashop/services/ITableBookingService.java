package com.example.pizzashop.services;

import com.example.pizzashop.dtos.TableBookingDTO;
import com.example.pizzashop.exceptions.DataNotFoundException;
import com.example.pizzashop.models.TableBooking;
import com.example.pizzashop.responses.TableBookingResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ITableBookingService {
    TableBooking createBooking(TableBookingDTO tableBookingDTO) throws Exception;

    TableBooking updateTableBooking(long id, TableBookingDTO tableBookingDTO) throws DataNotFoundException;

    List<TableBooking> getAllTableBookings(String keyword);

    void deleteBooking( Long id);
}
