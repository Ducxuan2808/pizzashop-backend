package com.example.pizzashop.responses;

import com.example.pizzashop.models.Order;
import com.example.pizzashop.models.TableBooking;
import com.example.pizzashop.models.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TableBookingResponse {

    private Long id;

    @JsonProperty("user_id")
    private User user;

    @JsonProperty("name")
    private String name;

    @JsonProperty("table_number")
    private Integer tableNumber;

    @JsonProperty("number_of_people")
    private Integer numberOfPeople;

    @JsonProperty("booking_time")
    private Date bookingTime;

    @JsonProperty("status")
    private String status;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("note")
    private String note;

    @JsonProperty("is_active")
    private Boolean active;

    public static TableBookingResponse fromBooking(TableBooking tableBooking){
        TableBookingResponse tableBookingResponse = TableBookingResponse
                .builder()
                .id(tableBooking.getId())
                .user(tableBooking.getUser())
                .name(tableBooking.getName())
                .tableNumber(tableBooking.getTableNumber())
                .numberOfPeople(tableBooking.getNumberOfPeople())
                .bookingTime(tableBooking.getBookingTime())
                .status(tableBooking.getStatus())
                .phone(tableBooking.getPhone())
                .note(tableBooking.getNote())
                .active(tableBooking.getActive())
                .build();
        return  tableBookingResponse;
    }
}
