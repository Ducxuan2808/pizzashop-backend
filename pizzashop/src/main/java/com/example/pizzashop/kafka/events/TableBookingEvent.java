package com.example.pizzashop.kafka.events;

import com.example.pizzashop.models.TableBooking;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableBookingEvent {
    public static final String CREATED = "BOOKING_CREATED";
    public static final String UPDATED = "BOOKING_UPDATED";
    public static final String CANCELLED = "BOOKING_CANCELLED";

    private String eventType;
    private Long bookingId;
    private Long userId;
    private String name;
    private String phone;
    private Integer tableNumber;
    private Integer numberOfPeople;
    private Date bookingTime;
    private String status;
    private long occurredAt;

    public static TableBookingEvent of(String eventType, TableBooking booking) {
        return TableBookingEvent.builder()
                .eventType(eventType)
                .bookingId(booking.getId())
                .userId(booking.getUser() != null ? booking.getUser().getId() : null)
                .name(booking.getName())
                .phone(booking.getPhone())
                .tableNumber(booking.getTableNumber())
                .numberOfPeople(booking.getNumberOfPeople())
                .bookingTime(booking.getBookingTime())
                .status(booking.getStatus())
                .occurredAt(System.currentTimeMillis())
                .build();
    }
}
