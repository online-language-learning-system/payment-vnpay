package com.hub.payment_vnpay.model.dto;

import lombok.*;

import java.math.BigDecimal;

@Builder
public record VnPayRequestDto(
        Long orderId,
        BigDecimal amount,
        String orderInfo,
        String returnUrl
) {}