package com.example.pizzashop.responses;

import com.example.pizzashop.models.BaseType;
import com.example.pizzashop.models.OrderDetail;
import com.example.pizzashop.models.Pizza;
import com.example.pizzashop.models.Size;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailResponse {

    private Long id;

    @JsonProperty( "order_id")
    private Long orderId;

    @JsonProperty("pizza")
    private Pizza pizza;

    @JsonProperty("size")
    private Size size;

    @JsonProperty("base_type")
    private BaseType baseType;

    @JsonProperty("quantity")
    private int quantity;

    @JsonProperty("price")
    private Float price;

    @JsonProperty("note")
    private String note;

    public static OrderDetailResponse fromOrderDetail(OrderDetail orderDetail){
        return OrderDetailResponse.builder()
                .id(orderDetail.getId())
                .orderId(orderDetail.getOrder().getId())
                .pizza(orderDetail.getPizza())
                .size(orderDetail.getSize())
                .baseType(orderDetail.getBaseType())
                .price(orderDetail.getPrice())
                .quantity(orderDetail.getQuantity())
                .note(orderDetail.getNote())
                .build();

    }
}
