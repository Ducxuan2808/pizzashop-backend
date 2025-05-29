package com.example.pizzashop.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentDTO {

    @JsonProperty("amount")
    private Long amount; // Số tiền cần thanh toán

    @JsonProperty("bankCode")
    private String bankCode; // Mã ngân hàng

    @JsonProperty("language")
    private String language; // Ngôn ngữ giao diện thanh toán (vd: "vn", "en")
}
