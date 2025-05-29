package com.example.pizzashop.controllers;

import com.example.pizzashop.components.LocalizationUtils;
import com.example.pizzashop.dtos.SizeDTO;
import com.example.pizzashop.dtos.TableBookingDTO;
import com.example.pizzashop.models.Size;
import com.example.pizzashop.models.TableBooking;
import com.example.pizzashop.responses.TableBookingResponse;
import com.example.pizzashop.services.ITableBookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("tablebookings")
@RequiredArgsConstructor
public class TableBookingController {
    private final ITableBookingService tableBookingService;
    private final LocalizationUtils localizationUtils;

    @PostMapping("")
    public ResponseEntity<?> createBooking(
            @Valid @RequestBody TableBookingDTO tableBookingDTO,
            BindingResult result){
        try{
            if(result.hasErrors()){
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            TableBooking tableBooking = tableBookingService.createBooking(tableBookingDTO);
            return ResponseEntity.ok(tableBooking);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("")
    public ResponseEntity<List<TableBooking>> getAllBookings(
            @RequestParam(defaultValue = "") String keyword
    ){
        List<TableBooking> bookings =  tableBookingService.getAllTableBookings(keyword);
        return ResponseEntity.ok(bookings);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTableBooing(@PathVariable Long id,
                                        @RequestBody TableBookingDTO tableBookingDTO){
        try {
            TableBooking existingBooking =  tableBookingService.updateTableBooking(id,tableBookingDTO);
            return ResponseEntity.ok(existingBooking);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTAbleBooking(
            @Valid @PathVariable Long id){
        //Xoa mem: active =0
        tableBookingService.deleteBooking(id);
        return ResponseEntity.ok(localizationUtils.getLocalizationMessage(
                "Xóa đơn hàng với mã: {0} thành công", id));
    }
}
