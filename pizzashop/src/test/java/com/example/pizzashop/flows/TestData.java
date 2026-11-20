package com.example.pizzashop.flows;

import com.example.pizzashop.dtos.CartItemDTO;
import com.example.pizzashop.dtos.OrderDTO;
import com.example.pizzashop.dtos.PizzaDTO;
import com.example.pizzashop.dtos.TableBookingDTO;
import com.example.pizzashop.dtos.UserDTO;
import com.example.pizzashop.models.BaseType;
import com.example.pizzashop.models.Order;
import com.example.pizzashop.models.Pizza;
import com.example.pizzashop.models.Role;
import com.example.pizzashop.models.Size;
import com.example.pizzashop.models.TableBooking;
import com.example.pizzashop.models.User;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

/**
 * Dữ liệu mẫu dùng chung cho các test luồng nghiệp vụ.
 */
public final class TestData {
    public static final String PHONE = "0987654321";

    private TestData() {
    }

    public static Role role(long id, String name) {
        Role role = new Role();
        role.setId(id);
        role.setRoleName(name);
        return role;
    }

    public static User user(long id, Role role) {
        User user = User.builder()
                .id(id)
                .username("user" + id)
                .fullName("Nguyen Van A")
                .email("a@example.com")
                .phone(PHONE)
                .password("encoded-password")
                .active(true)
                .build();
        user.setRole(role);
        return user;
    }

    public static Pizza pizza(long id) {
        return Pizza.builder()
                .id(id)
                .name("Pizza Hải Sản")
                .title("Hải sản")
                .basePrice(150000f)
                .thumbnail("seafood.png")
                .description("Tôm, mực")
                .active(true)
                .build();
    }

    public static PizzaDTO pizzaDTO(String name) {
        return PizzaDTO.builder()
                .name(name)
                .title("Title " + name)
                .basePrice(200000f)
                .thumbnail("thumb.png")
                .description("Desc")
                .build();
    }

    public static Size size(long id) {
        Size size = new Size();
        size.setId(id);
        return size;
    }

    public static BaseType baseType(long id) {
        BaseType baseType = new BaseType();
        baseType.setId(id);
        return baseType;
    }

    public static CartItemDTO cartItem(long pizzaId, int quantity) {
        return CartItemDTO.builder()
                .pizzaId(pizzaId)
                .sizeId(1L)
                .baseId(1L)
                .quantity(quantity)
                .price(150000f)
                .note("ít cay")
                .build();
    }

    public static OrderDTO orderDTO(Long userId, List<CartItemDTO> items) {
        return OrderDTO.builder()
                .userId(userId)
                .fullName("Nguyen Van A")
                .email("a@example.com")
                .phoneOrder(PHONE)
                .orderType(Order.TYPE_ONLINE)
                .deliveryAddress("Hà Nội")
                .deliveryPhone(PHONE)
                .totalPrice(300000f)
                .discountAmount(0f)
                .paymentMethod("COD")
                .shippingTime(Date.valueOf(LocalDate.now()))
                .cartItems(items)
                .build();
    }

    public static Order order(long id, String status) {
        return Order.builder()
                .id(id)
                .fullName("Nguyen Van A")
                .phoneOrder(PHONE)
                .orderType(Order.TYPE_ONLINE)
                .totalPrice(300000f)
                .status(status)
                .active(true)
                .build();
    }

    public static UserDTO userDTO(long roleId) {
        return UserDTO.builder()
                .username("newuser")
                .fullName("Tran Thi B")
                .email("b@example.com")
                .phone(PHONE)
                .password("123456")
                .retypePassword("123456")
                .address("Hà Nội")
                .roleId(roleId)
                .build();
    }

    public static TableBookingDTO bookingDTO(Long userId) {
        return TableBookingDTO.builder()
                .userId(userId)
                .name("Nguyen Van A")
                .phone(PHONE)
                .tableNumber(5)
                .numberOfPeople(4)
                .bookingTime(new java.util.Date())
                .note("Gần cửa sổ")
                .status(TableBooking.BOOK_RESERVED)
                .active(true)
                .build();
    }

    public static TableBooking booking(long id) {
        return TableBooking.builder()
                .id(id)
                .name("Nguyen Van A")
                .phone(PHONE)
                .tableNumber(5)
                .numberOfPeople(4)
                .status(TableBooking.BOOK_RESERVED)
                .active(true)
                .build();
    }
}
