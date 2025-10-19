package com.hub.payment_vnpay.controller;

import com.hub.payment_vnpay.model.dto.VnpayRequestDto;
import com.hub.payment_vnpay.model.dto.VnpayResponseDto;
import com.hub.payment_vnpay.service.VnPayService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/vnpay")
@RequiredArgsConstructor
public class VnPayController {

    private final VnPayService vnpayService;

    @PostMapping("/create-payment")
    public VnpayResponseDto createPayment(@RequestBody VnpayRequestDto requestDto) {
        return vnpayService.createPaymentUrl(requestDto);
    }

    // Callback client
    @GetMapping("/callback")
    public void callback(@RequestParam Map<String, String> params) {
         vnpayService.handlePayment(params);
    }

    // IPN server
    @PostMapping("/ipn")
    public void ipn(@RequestParam Map<String, String> params) {
         vnpayService.handlePayment(params);
    }
}