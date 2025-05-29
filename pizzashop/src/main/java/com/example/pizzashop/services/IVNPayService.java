package com.example.pizzashop.services;

import com.example.pizzashop.dtos.PaymentDTO;
import jakarta.servlet.http.HttpServletRequest;

public interface IVNPayService {
    String createPaymentUrl(PaymentDTO paymentRequest, HttpServletRequest request);
}
