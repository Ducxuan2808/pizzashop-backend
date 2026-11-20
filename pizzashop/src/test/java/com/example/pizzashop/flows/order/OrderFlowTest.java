package com.example.pizzashop.flows.order;

import com.example.pizzashop.dtos.MembershipDTO;
import com.example.pizzashop.dtos.OrderDTO;
import com.example.pizzashop.exceptions.DataNotFoundException;
import com.example.pizzashop.flows.TestData;
import com.example.pizzashop.kafka.KafkaEventPublisher;
import com.example.pizzashop.kafka.KafkaTopics;
import com.example.pizzashop.kafka.events.OrderEvent;
import com.example.pizzashop.models.Membership;
import com.example.pizzashop.models.Order;
import com.example.pizzashop.models.OrderDetail;
import com.example.pizzashop.models.User;
import com.example.pizzashop.repositories.BaseTypeRepository;
import com.example.pizzashop.repositories.MembershipRepository;
import com.example.pizzashop.repositories.OrderDetailRepository;
import com.example.pizzashop.repositories.OrderRepository;
import com.example.pizzashop.repositories.PizzaRepository;
import com.example.pizzashop.repositories.SizeRepository;
import com.example.pizzashop.repositories.UserRepository;
import com.example.pizzashop.services.MembershipService;
import com.example.pizzashop.services.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Chức năng Đơn hàng")
class OrderFlowTest {
    @Mock UserRepository userRepository;
    @Mock OrderRepository orderRepository;
    @Mock OrderDetailRepository orderDetailRepository;
    @Mock PizzaRepository pizzaRepository;
    @Mock SizeRepository sizeRepository;
    @Mock BaseTypeRepository baseTypeRepository;
    @Mock MembershipRepository membershipRepository;
    @Mock MembershipService membershipService;
    @Mock KafkaEventPublisher kafkaEventPublisher;

    @InjectMocks OrderService orderService;

    private OrderEvent capturePublishedEvent() {
        ArgumentCaptor<OrderEvent> captor = ArgumentCaptor.forClass(OrderEvent.class);
        verify(kafkaEventPublisher).publish(eq(KafkaTopics.ORDER_EVENTS), any(), captor.capture());
        return captor.getValue();
    }

    private void stubCatalog(long pizzaId) {
        when(pizzaRepository.findById(pizzaId)).thenReturn(Optional.of(TestData.pizza(pizzaId)));
        when(sizeRepository.findById(1L)).thenReturn(Optional.of(TestData.size(1L)));
        when(baseTypeRepository.findById(1L)).thenReturn(Optional.of(TestData.baseType(1L)));
    }

    @Nested
    @DisplayName("Luồng tạo đơn hàng")
    class CreateOrder {
        @Test
        @DisplayName("Khách vãng lai đặt hàng: lưu đơn Pending, lưu chi tiết và gửi ORDER_CREATED")
        void guestCreatesOrder() throws Exception {
            OrderDTO dto = TestData.orderDTO(99L, List.of(TestData.cartItem(1L, 2), TestData.cartItem(1L, 1)));
            when(userRepository.findById(99L)).thenReturn(Optional.empty());
            stubCatalog(1L);
            when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
                Order o = inv.getArgument(0);
                o.setId(10L);
                return o;
            });

            Order order = orderService.createOrder(dto);

            assertThat(order.getId()).isEqualTo(10L);
            assertThat(order.getStatus()).isEqualTo(Order.STATUS_PENDING);
            assertThat(order.getActive()).isTrue();
            assertThat(order.getUser()).isNull();
            assertThat(order.getPhoneOrder()).isEqualTo(dto.getPhoneOrder());

            @SuppressWarnings("unchecked")
            ArgumentCaptor<List<OrderDetail>> details = ArgumentCaptor.forClass(List.class);
            verify(orderDetailRepository).saveAll(details.capture());
            assertThat(details.getValue()).hasSize(2)
                    .allSatisfy(d -> assertThat(d.getOrder()).isSameAs(order));

            OrderEvent event = capturePublishedEvent();
            assertThat(event.getEventType()).isEqualTo(OrderEvent.CREATED);
            assertThat(event.getOrderId()).isEqualTo(10L);
            assertThat(event.getItemCount()).isEqualTo(2);
            assertThat(event.getStatus()).isEqualTo(Order.STATUS_PENDING);
        }

        @Test
        @DisplayName("Khách hàng đã đăng nhập (role USER): lấy số điện thoại từ tài khoản")
        void loggedInCustomerUsesAccountPhone() throws Exception {
            User customer = TestData.user(5L, TestData.role(2L, "USER"));
            customer.setPhone("0911111111");
            OrderDTO dto = TestData.orderDTO(5L, List.of(TestData.cartItem(1L, 1)));
            when(userRepository.findById(5L)).thenReturn(Optional.of(customer));
            stubCatalog(1L);

            Order order = orderService.createOrder(dto);

            assertThat(order.getUser()).isSameAs(customer);
            assertThat(order.getPhoneOrder()).isEqualTo("0911111111");
            assertThat(capturePublishedEvent().getUserId()).isEqualTo(5L);
        }

        @Test
        @DisplayName("Pizza không tồn tại: báo lỗi và không gửi event")
        void pizzaNotFound() {
            OrderDTO dto = TestData.orderDTO(1L, List.of(TestData.cartItem(404L, 1)));
            when(pizzaRepository.findById(404L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> orderService.createOrder(dto))
                    .isInstanceOf(DataNotFoundException.class)
                    .hasMessageContaining("404");
            verify(orderDetailRepository, never()).saveAll(anyList());
            verifyNoInteractions(kafkaEventPublisher);
        }

        @Test
        @DisplayName("Ngày giao trong quá khứ: báo lỗi và không lưu đơn")
        void shippingDateInPast() {
            OrderDTO dto = TestData.orderDTO(1L, List.of(TestData.cartItem(1L, 1)));
            dto.setShippingTime(Date.valueOf(LocalDate.now().minusDays(2)));

            assertThatThrownBy(() -> orderService.createOrder(dto))
                    .isInstanceOf(DataNotFoundException.class);
            verify(orderRepository, never()).save(any());
            verifyNoInteractions(kafkaEventPublisher);
        }
    }

    @Nested
    @DisplayName("Luồng cập nhật đơn hàng")
    class UpdateOrder {
        @Test
        @DisplayName("Đổi trạng thái: gửi ORDER_STATUS_CHANGED kèm trạng thái cũ")
        void statusChanged() throws Exception {
            Order existing = TestData.order(10L, Order.STATUS_PENDING);
            when(orderRepository.findById(10L)).thenReturn(Optional.of(existing));
            OrderDTO dto = TestData.orderDTO(1L, List.of());
            dto.setStatus(Order.STATUS_PREPARING);

            Order updated = orderService.updateOrder(10L, dto);

            assertThat(updated.getStatus()).isEqualTo(Order.STATUS_PREPARING);
            verify(orderRepository).save(existing);
            OrderEvent event = capturePublishedEvent();
            assertThat(event.getEventType()).isEqualTo(OrderEvent.STATUS_CHANGED);
            assertThat(event.getPreviousStatus()).isEqualTo(Order.STATUS_PENDING);
            assertThat(event.getStatus()).isEqualTo(Order.STATUS_PREPARING);
        }

        @Test
        @DisplayName("Không đổi trạng thái: gửi ORDER_UPDATED")
        void updatedWithoutStatusChange() throws Exception {
            when(orderRepository.findById(10L)).thenReturn(Optional.of(TestData.order(10L, Order.STATUS_PENDING)));
            OrderDTO dto = TestData.orderDTO(1L, List.of());
            dto.setStatus(Order.STATUS_PENDING);
            dto.setNote("Giao trước 12h");

            Order updated = orderService.updateOrder(10L, dto);

            assertThat(updated.getNote()).isEqualTo("Giao trước 12h");
            assertThat(capturePublishedEvent().getEventType()).isEqualTo(OrderEvent.UPDATED);
        }

        @Test
        @DisplayName("Hoàn thành đơn: cập nhật hạng thành viên theo tổng chi tiêu")
        void completedOrderUpgradesMembership() throws Exception {
            Order existing = TestData.order(10L, Order.STATUS_DELIVERING);
            Membership membership = Membership.builder().id(7L).phone(TestData.PHONE)
                    .membershipTier(Membership.MEMBER_SILVER).totalSpent(1500000f).build();
            when(orderRepository.findById(10L)).thenReturn(Optional.of(existing));
            when(membershipRepository.getMembershipByPhone(TestData.PHONE)).thenReturn(Optional.of(membership));
            when(orderRepository.sumTotalAmount(TestData.PHONE)).thenReturn(3500000f);
            OrderDTO dto = TestData.orderDTO(1L, List.of());
            dto.setStatus(Order.STATUS_COMPLETED);

            orderService.updateOrder(10L, dto);

            ArgumentCaptor<MembershipDTO> captor = ArgumentCaptor.forClass(MembershipDTO.class);
            verify(membershipService).updateMembership(eq(7L), captor.capture());
            assertThat(captor.getValue().getMembershipTier()).isEqualTo(Membership.MEMBER_GOLD);
            assertThat(captor.getValue().getDiscountRate()).isEqualTo(Membership.DISCOUNT_GOLD);
            assertThat(captor.getValue().getTotalSpent()).isEqualTo(3500000f);
            assertThat(capturePublishedEvent().getStatus()).isEqualTo(Order.STATUS_COMPLETED);
        }

        @Test
        @DisplayName("Đơn không tồn tại: báo lỗi")
        void orderNotFound() {
            when(orderRepository.findById(404L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> orderService.updateOrder(404L, TestData.orderDTO(1L, List.of())))
                    .isInstanceOf(DataNotFoundException.class);
            verifyNoInteractions(kafkaEventPublisher);
        }
    }

    @Nested
    @DisplayName("Luồng huỷ đơn hàng")
    class CancelOrder {
        @Test
        @DisplayName("Huỷ đơn: xoá mềm, trạng thái Cancelled và gửi ORDER_CANCELLED")
        void cancelOrder() {
            Order existing = TestData.order(10L, Order.STATUS_PENDING);
            when(orderRepository.findById(10L)).thenReturn(Optional.of(existing));

            orderService.deleteOrder(10L);

            assertThat(existing.getStatus()).isEqualTo(Order.STATUS_CANCELLED);
            assertThat(existing.getActive()).isFalse();
            verify(orderRepository).save(existing);
            OrderEvent event = capturePublishedEvent();
            assertThat(event.getEventType()).isEqualTo(OrderEvent.CANCELLED);
            assertThat(event.getPreviousStatus()).isEqualTo(Order.STATUS_PENDING);
        }

        @Test
        @DisplayName("Đơn không tồn tại: không làm gì")
        void cancelMissingOrder() {
            when(orderRepository.findById(404L)).thenReturn(Optional.empty());

            orderService.deleteOrder(404L);

            verify(orderRepository, never()).save(any());
            verifyNoInteractions(kafkaEventPublisher);
        }
    }

    @Nested
    @DisplayName("Quy tắc hạng thành viên")
    class MembershipTierRules {
        @ParameterizedTest(name = "Chi tiêu {0} => hạng {1}")
        @CsvSource({
                "0, Bronze",
                "999999, Bronze",
                "1000000, Silver",
                "3000000, Gold",
                "9999999, Gold",
                "10000000, Platinum"
        })
        void tierByTotalSpent(float spent, String expectedTier) {
            assertThat(OrderService.getTierByTotalSpent(spent)).isEqualTo(expectedTier);
        }

        @Test
        void discountByTier() {
            assertThat(OrderService.getDiscountByTier(Membership.MEMBER_BRONZE)).isEqualTo(Membership.DISCOUNT_BRONZE);
            assertThat(OrderService.getDiscountByTier(Membership.MEMBER_PLATINUM)).isEqualTo(Membership.DISCOUNT_PLATINUM);
            assertThat(OrderService.getDiscountByTier(null)).isNull();
        }
    }
}
