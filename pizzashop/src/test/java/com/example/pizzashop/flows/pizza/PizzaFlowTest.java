package com.example.pizzashop.flows.pizza;

import com.example.pizzashop.dtos.PizzaDTO;
import com.example.pizzashop.exceptions.DataNotFoundException;
import com.example.pizzashop.flows.TestData;
import com.example.pizzashop.kafka.KafkaEventPublisher;
import com.example.pizzashop.kafka.KafkaTopics;
import com.example.pizzashop.kafka.events.PizzaEvent;
import com.example.pizzashop.models.Pizza;
import com.example.pizzashop.repositories.OrderDetailRepository;
import com.example.pizzashop.repositories.PizzaImageRepository;
import com.example.pizzashop.repositories.PizzaRepository;
import com.example.pizzashop.services.PizzaService;
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
@DisplayName("Chức năng Pizza (sản phẩm)")
class PizzaFlowTest {
    @Mock PizzaRepository pizzaRepository;
    @Mock PizzaImageRepository pizzaImageRepository;
    @Mock OrderDetailRepository orderDetailRepository;
    @Mock KafkaEventPublisher kafkaEventPublisher;

    @InjectMocks PizzaService pizzaService;

    private PizzaEvent capturePublishedEvent() {
        ArgumentCaptor<PizzaEvent> captor = ArgumentCaptor.forClass(PizzaEvent.class);
        verify(kafkaEventPublisher).publish(eq(KafkaTopics.PIZZA_EVENTS), any(), captor.capture());
        return captor.getValue();
    }

    @Nested
    @DisplayName("Luồng tạo pizza")
    class Create {
        @Test
        @DisplayName("Tạo pizza: active = true, lưu DB và gửi PIZZA_CREATED")
        void createPizza() throws Exception {
            PizzaDTO dto = TestData.pizzaDTO("Pizza Bò");
            when(pizzaRepository.save(any(Pizza.class))).thenAnswer(inv -> {
                Pizza p = inv.getArgument(0);
                p.setId(3L);
                return p;
            });

            Pizza created = pizzaService.createPizza(dto);

            assertThat(created.getId()).isEqualTo(3L);
            assertThat(created.getName()).isEqualTo("Pizza Bò");
            assertThat(created.getBasePrice()).isEqualTo(200000f);
            assertThat(created.getActive()).isTrue();
            PizzaEvent event = capturePublishedEvent();
            assertThat(event.getEventType()).isEqualTo(PizzaEvent.CREATED);
            assertThat(event.getPizzaId()).isEqualTo(3L);
            assertThat(event.getName()).isEqualTo("Pizza Bò");
        }

        @Test
        @DisplayName("Kiểm tra trùng tên pizza")
        void existByName() {
            when(pizzaRepository.existsByName("Pizza Bò")).thenReturn(true);
            when(pizzaRepository.existsByName("Pizza Gà")).thenReturn(false);

            assertThat(pizzaService.existByName("Pizza Bò")).isTrue();
            assertThat(pizzaService.existByName("Pizza Gà")).isFalse();
        }
    }

    @Nested
    @DisplayName("Luồng xem / cập nhật / xoá pizza")
    class Manage {
        @Test
        @DisplayName("Xem chi tiết pizza không tồn tại: báo lỗi")
        void getMissingPizza() {
            when(pizzaRepository.getDetailPizza(404L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> pizzaService.getPizzaById(404L))
                    .isInstanceOf(DataNotFoundException.class);
        }

        @Test
        @DisplayName("Cập nhật pizza: đổi thông tin và gửi PIZZA_UPDATED")
        void updatePizza() throws Exception {
            Pizza existing = TestData.pizza(1L);
            when(pizzaRepository.getDetailPizza(1L)).thenReturn(Optional.of(existing));
            when(pizzaRepository.save(existing)).thenReturn(existing);

            Pizza updated = pizzaService.updatePizza(1L, TestData.pizzaDTO("Pizza Mới"));

            assertThat(updated.getName()).isEqualTo("Pizza Mới");
            assertThat(updated.getBasePrice()).isEqualTo(200000f);
            PizzaEvent event = capturePublishedEvent();
            assertThat(event.getEventType()).isEqualTo(PizzaEvent.UPDATED);
            assertThat(event.getName()).isEqualTo("Pizza Mới");
        }

        @Test
        @DisplayName("Cập nhật pizza không tồn tại: báo lỗi, không gửi event")
        void updateMissingPizza() {
            when(pizzaRepository.getDetailPizza(404L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> pizzaService.updatePizza(404L, TestData.pizzaDTO("X")))
                    .isInstanceOf(DataNotFoundException.class);
            verify(pizzaRepository, never()).save(any());
            verifyNoInteractions(kafkaEventPublisher);
        }

        @Test
        @DisplayName("Xoá pizza: xoá mềm (active = false) và gửi PIZZA_DELETED")
        void deletePizza() throws Exception {
            Pizza existing = TestData.pizza(1L);
            when(pizzaRepository.getDetailPizza(1L)).thenReturn(Optional.of(existing));

            pizzaService.deletePizza(1L);

            assertThat(existing.getActive()).isFalse();
            verify(pizzaRepository).save(existing);
            PizzaEvent event = capturePublishedEvent();
            assertThat(event.getEventType()).isEqualTo(PizzaEvent.DELETED);
            assertThat(event.getActive()).isFalse();
        }
    }
}
