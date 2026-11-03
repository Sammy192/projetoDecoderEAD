package com.ead.payment.dto;

import com.ead.payment.enums.PaymentStatusControlEnum;
import com.ead.payment.models.PaymentModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentResponseDTO(
        UUID paymentId,
        PaymentStatusControlEnum paymentControl,
        LocalDateTime paymentRequestDate,
        LocalDateTime paymentCompletionDate,
        LocalDateTime paymentExpirationDate,
        String lastDigitsCreditCard,
        BigDecimal valuePaid,
        String paymentMessage,
        boolean recurrence
) {
    public PaymentResponseDTO(PaymentModel model) {
        this(
                model.getPaymentId(),
                model.getPaymentControl(),
                model.getPaymentRequestDate(),
                model.getPaymentCompletionDate(),
                model.getPaymentExpirationDate(),
                model.getLastDigitsCreditCard(),
                model.getValuePaid(),
                model.getPaymentMessage(),
                model.isRecurrence()
        );
    }
}