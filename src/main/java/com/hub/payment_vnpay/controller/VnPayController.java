package com.hub.payment_vnpay.controller;

import com.hub.payment_vnpay.service.VnPayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/vnpay")
@RequiredArgsConstructor
public class VnPayController {

    private final VnPayService vnpayService;

    @GetMapping("/get-payment-url")
    public ResponseEntity<String> getPaymentUrl() {
        return ResponseEntity.ok("");
    }

//    @PostMapping("/create-payment")
//    public VnPayResponseDto createPayment(@RequestBody VnPayRequestDto requestDto) {
//        return vnpayService.createPaymentUrl(requestDto);
//    }

//    Callback client
//    @GetMapping("/callback")
//    public void callback(@RequestParam Map<String, String> params) {
//         vnpayService.handlePayment(params);
//    }

    /**
     * IPN Server
     *
     * @param params
     */
    @PostMapping("/ipn")
    public void ipn(@RequestParam Map<String, String> params) {
         vnpayService.handlePayment(params);
    }
}