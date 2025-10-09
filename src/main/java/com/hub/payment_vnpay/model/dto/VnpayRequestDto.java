package com.hub.payment_vnpay.model.dto;

import java.math.BigDecimal;

public record VnpayRequestDto(
        Long orderId,
        BigDecimal amount,
        String orderInfo,
        String returnUrl
) {}