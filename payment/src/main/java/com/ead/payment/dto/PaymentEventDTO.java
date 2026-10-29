package com.ead.payment.dto;

import com.ead.payment.models.PaymentModel;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class PaymentEventDTO {

    private UUID paymentId;
    private UUID userId;
    private String paymentControl;
    private LocalDateTime paymentRequestDate;
    private LocalDateTime paymentCompletionDate;
    private LocalDateTime paymentExpirationDate;
    private String lastDigitsCreditCard;
    private BigDecimal valuePaid;
    private String paymentMessage;
    private boolean recurrence;


    public PaymentEventDTO() {
    }

    public PaymentEventDTO(PaymentModel paymentModel) {
        BeanUtils.copyProperties(paymentModel, this);
        this.paymentControl = paymentModel.getPaymentControl().toString();
        this.userId = paymentModel.getUser().getUserId();
    }
}
