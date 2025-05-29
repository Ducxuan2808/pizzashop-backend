package com.example.pizzashop.dtos;

import com.example.pizzashop.models.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TableBookingDTO {
    @JsonProperty("user_id")
    private Long userId;

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
}
