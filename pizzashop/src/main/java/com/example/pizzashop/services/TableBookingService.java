package com.example.pizzashop.services;

import com.example.pizzashop.dtos.TableBookingDTO;
import com.example.pizzashop.exceptions.DataNotFoundException;
import com.example.pizzashop.models.Order;
import com.example.pizzashop.models.Pizza;
import com.example.pizzashop.models.TableBooking;
import com.example.pizzashop.models.User;
import com.example.pizzashop.repositories.TableBookingRepository;
import com.example.pizzashop.repositories.UserRepository;
import com.example.pizzashop.responses.TableBookingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TableBookingService implements ITableBookingService {
    private final TableBookingRepository tableBookingRepository;
    private final UserRepository userRepository;
    @Override
    @Transactional
    public TableBooking createBooking(TableBookingDTO tableBookingDTO) throws Exception{
        User existUser =  userRepository.findById(tableBookingDTO.getUserId())
                .orElse(null);
        TableBooking tableBooking = TableBooking.builder()
                .user(existUser)
                .name(tableBookingDTO.getName())
                .phone(tableBookingDTO.getPhone())
                .numberOfPeople(tableBookingDTO.getNumberOfPeople())
                .bookingTime(tableBookingDTO.getBookingTime())
                .tableNumber(tableBookingDTO.getTableNumber())
                .status(TableBooking.BOOK_RESERVED)
                .note(tableBookingDTO.getNote())
                .active(true)
                .build();
        return tableBookingRepository.save(tableBooking);
    }

    @Override
    @Transactional
    public TableBooking updateTableBooking(long id, TableBookingDTO tableBookingDTO) throws DataNotFoundException{
        User existUser =  userRepository.findById(tableBookingDTO.getUserId())
                .orElse(null);
        TableBooking existBooking = tableBookingRepository.findById(id)
                .orElseThrow(()->new DataNotFoundException("Cannot find booking with id: "+id));
        if(existBooking!=null){
            existBooking.setName(tableBookingDTO.getName());
            existBooking.setUser(existUser);
            existBooking.setPhone(tableBookingDTO.getPhone());
            existBooking.setNumberOfPeople(tableBookingDTO.getNumberOfPeople());
            existBooking.setBookingTime(tableBookingDTO.getBookingTime());
            existBooking.setTableNumber(tableBookingDTO.getTableNumber());
            existBooking.setStatus(tableBookingDTO.getStatus());
            existBooking.setName(tableBookingDTO.getNote());
            existBooking.setActive(tableBookingDTO.getActive());
            return tableBookingRepository.save(existBooking);
        }
        return null;

    }

    @Override
    public List<TableBooking> getAllTableBookings(String keyword) {

        return tableBookingRepository.getAllTableBookings(keyword);
    }

    @Override
    public void deleteBooking(Long id) {
        TableBooking tableBooking = tableBookingRepository.findById(id).orElse(null);
        //khong xoa cung => xoa mem
        if(tableBooking !=null){
            tableBooking.setActive(false);
            tableBookingRepository.save(tableBooking);
        }
    }
}
