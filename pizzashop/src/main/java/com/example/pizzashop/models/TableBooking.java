package com.example.pizzashop.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "tablebooking")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TableBooking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @JoinColumn(name = "name")
    private String name;

    @Column(name = "table_number")
    @JsonProperty("table_number")
    private Integer tableNumber;

    @Column(name = "number_of_people", nullable = false)
    @JsonProperty("number_of_people")
    private Integer numberOfPeople;

    @Column(name = "booking_time")
    @JsonProperty("booking_time")
    private Date bookingTime;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "note")
    private String note;

    @Column(name = "is_active")
    private Boolean active;

    public static final String BOOK_RESERVED = "Reserved";
    public static final String BOOK_COMPLETED = "Completed";
    public static final String BOOK_CANCELLED = "Cancelled";
    public static final String BOOK_WALKIN = "Walk-in";
}
