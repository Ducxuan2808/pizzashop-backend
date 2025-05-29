package com.example.pizzashop.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(name = "email")
    private String email;

    @Column(name = "phone_order", length = 20)
    private String phoneOrder;

    @Column(name = "order_type", nullable = false)
    private String orderType;

    @Column(name = "total_price", nullable = false)
    private Float totalPrice;

    @Column(name = "discount_amount")
    private Float discountAmount;

    @Column(name = "delivery_address", columnDefinition = "TEXT")
    private String deliveryAddress;

    @Column(name = "delivery_phone", length = 20)
    private String deliveryPhone;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "order_time")
    private Date orderTime;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,  fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<OrderDetail> orderDetails;

    @Column(name = "table_number")
    private Integer table;

    @Column(name = "note")
    private String note;

    @Column(name = "is_active")
    private Boolean active;//thuộc về admin

    @Column(name = "shipping_time")
    private java.util.Date shippingTime;

    @Column(name = "payment_method")
    private String paymentMethod;

    public static final String TYPE_ONLINE = "Online";
    public static final String TYPE_DINEIN = "Dine-in";
    public static final String TYPE_TAKEAWAY = "Takeaway";

    public static final String STATUS_PENDING = "Pending";
    public static final String STATUS_PREPARING = "Preparing";
    public static final String STATUS_DELIVERING = "Delivering";
    public static final String STATUS_COMPLETED = "Completed";
    public static final String STATUS_CANCELLED = "Cancelled";

    public static final Float VALUE_1000000  = 1000000f;
    public static final Float VALUE_3000000 = 3000000f;
    public static final Float VALUE_10000000 = 10000000f;

    public static final Float DISCOUNT_BRONZE  = 0.0f;
    public static final Float DISCOUNT_SILVER = 1.5f;
    public static final Float DISCOUNT_GOLD = 3f;
    public static final Float DISCOUNT_PLATINUM = 5f;
}