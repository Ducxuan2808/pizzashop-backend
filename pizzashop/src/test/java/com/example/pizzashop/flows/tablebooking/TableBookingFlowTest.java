package com.example.pizzashop.flows.tablebooking;

import com.example.pizzashop.dtos.TableBookingDTO;
import com.example.pizzashop.exceptions.DataNotFoundException;
import com.example.pizzashop.flows.TestData;
import com.example.pizzashop.kafka.KafkaEventPublisher;
import com.example.pizzashop.kafka.KafkaTopics;
import com.example.pizzashop.kafka.events.TableBookingEvent;
import com.example.pizzashop.models.Role;
import com.example.pizzashop.models.TableBooking;
import com.example.pizzashop.models.User;
import com.example.pizzashop.repositories.TableBookingRepository;
import com.example.pizzashop.repositories.UserRepository;
import com.example.pizzashop.services.TableBookingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Chức năng Đặt bàn")
class TableBookingFlowTest {
    @Mock TableBookingRepository tableBookingRepository;
    @Mock UserRepository userRepository;
    @Mock KafkaEventPublisher kafkaEventPublisher;

    @InjectMocks TableBookingService tableBookingService;

    private TableBookingEvent capturePublishedEvent() {
        ArgumentCaptor<TableBookingEvent> captor = ArgumentCaptor.forClass(TableBookingEvent.class);
        verify(kafkaEventPublisher).publish(eq(KafkaTopics.TABLE_BOOKING_EVENTS), any(), captor.capture());
        return captor.getValue();
    }

    @Nested
    @DisplayName("Luồng đặt bàn")
    class Create {
        @Test
        @DisplayName("Đặt bàn: trạng thái Reserved, active và gửi BOOKING_CREATED")
        void createBooking() throws Exception {
            User user = TestData.user(1L, TestData.role(2L, Role.USER));
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(tableBookingRepository.save(any(TableBooking.class))).thenAnswer(inv -> {
                TableBooking b = inv.getArgument(0);
                b.setId(20L);
                return b;
            });

            TableBooking booking = tableBookingService.createBooking(TestData.bookingDTO(1L));

            assertThat(booking.getStatus()).isEqualTo(TableBooking.BOOK_RESERVED);
            assertThat(booking.getActive()).isTrue();
            assertThat(booking.getUser()).isSameAs(user);
            assertThat(booking.getTableNumber()).isEqualTo(5);
            TableBookingEvent event = capturePublishedEvent();
            assertThat(event.getEventType()).isEqualTo(TableBookingEvent.CREATED);
            assertThat(event.getBookingId()).isEqualTo(20L);
            assertThat(event.getUserId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Khách không có tài khoản vẫn đặt được bàn")
        void guestBooking() throws Exception {
            when(userRepository.findById(99L)).thenReturn(Optional.empty());
            when(tableBookingRepository.save(any(TableBooking.class))).thenAnswer(inv -> inv.getArgument(0));

            TableBooking booking = tableBookingService.createBooking(TestData.bookingDTO(99L));

            assertThat(booking.getUser()).isNull();
            assertThat(capturePublishedEvent().getUserId()).isNull();
        }
    }

    @Nested
    @DisplayName("Luồng cập nhật / huỷ đặt bàn")
    class Manage {
        @Test
        @DisplayName("Cập nhật đặt bàn: đổi trạng thái và gửi BOOKING_UPDATED")
        void updateBooking() throws Exception {
            TableBooking existing = TestData.booking(20L);
            when(tableBookingRepository.findById(20L)).thenReturn(Optional.of(existing));
            when(tableBookingRepository.save(existing)).thenReturn(existing);
            TableBookingDTO dto = TestData.bookingDTO(1L);
            dto.setStatus(TableBooking.BOOK_COMPLETED);
            dto.setNumberOfPeople(6);

            TableBooking updated = tableBookingService.updateTableBooking(20L, dto);

            assertThat(updated.getStatus()).isEqualTo(TableBooking.BOOK_COMPLETED);
            assertThat(updated.getNumberOfPeople()).isEqualTo(6);
            TableBookingEvent event = capturePublishedEvent();
            assertThat(event.getEventType()).isEqualTo(TableBookingEvent.UPDATED);
            assertThat(event.getStatus()).isEqualTo(TableBooking.BOOK_COMPLETED);
        }

        @Test
        @DisplayName("Cập nhật đặt bàn không tồn tại: báo lỗi")
        void updateMissingBooking() {
            when(tableBookingRepository.findById(404L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> tableBookingService.updateTableBooking(404L, TestData.bookingDTO(1L)))
                    .isInstanceOf(DataNotFoundException.class);
            verifyNoInteractions(kafkaEventPublisher);
        }

        @Test
        @DisplayName("Huỷ đặt bàn: xoá mềm và gửi BOOKING_CANCELLED")
        void cancelBooking() {
            TableBooking existing = TestData.booking(20L);
            when(tableBookingRepository.findById(20L)).thenReturn(Optional.of(existing));

            tableBookingService.deleteBooking(20L);

            assertThat(existing.getActive()).isFalse();
            verify(tableBookingRepository).save(existing);
            assertThat(capturePublishedEvent().getEventType()).isEqualTo(TableBookingEvent.CANCELLED);
        }

        @Test
        @DisplayName("Huỷ đặt bàn không tồn tại: không làm gì")
        void cancelMissingBooking() {
            when(tableBookingRepository.findById(404L)).thenReturn(Optional.empty());

            tableBookingService.deleteBooking(404L);

            verify(tableBookingRepository, never()).save(any());
            verifyNoInteractions(kafkaEventPublisher);
        }
    }
}
