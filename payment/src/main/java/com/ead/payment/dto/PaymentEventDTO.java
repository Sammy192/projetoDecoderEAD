package com.ead.payment.dto;

import com.ead.payment.models.PaymentModel;

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
        this.paymentId = paymentModel.getPaymentId();
        this.userId = paymentModel.getUser().getUserId();
        this.paymentControl = paymentModel.getPaymentControl().toString();
        this.paymentRequestDate = paymentModel.getPaymentRequestDate();
        this.paymentCompletionDate = paymentModel.getPaymentCompletionDate();
        this.paymentExpirationDate = paymentModel.getPaymentExpirationDate();
        this.lastDigitsCreditCard = paymentModel.getLastDigitsCreditCard();
        this.valuePaid = paymentModel.getValuePaid();
        this.paymentMessage = paymentModel.getPaymentMessage();
        this.recurrence = paymentModel.isRecurrence();
    }

    public UUID getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(UUID paymentId) {
        this.paymentId = paymentId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getPaymentControl() {
        return paymentControl;
    }

    public void setPaymentControl(String paymentControl) {
        this.paymentControl = paymentControl;
    }

    public LocalDateTime getPaymentRequestDate() {
        return paymentRequestDate;
    }

    public void setPaymentRequestDate(LocalDateTime paymentRequestDate) {
        this.paymentRequestDate = paymentRequestDate;
    }

    public LocalDateTime getPaymentCompletionDate() {
        return paymentCompletionDate;
    }

    public void setPaymentCompletionDate(LocalDateTime paymentCompletionDate) {
        this.paymentCompletionDate = paymentCompletionDate;
    }

    public LocalDateTime getPaymentExpirationDate() {
        return paymentExpirationDate;
    }

    public void setPaymentExpirationDate(LocalDateTime paymentExpirationDate) {
        this.paymentExpirationDate = paymentExpirationDate;
    }

    public String getLastDigitsCreditCard() {
        return lastDigitsCreditCard;
    }

    public void setLastDigitsCreditCard(String lastDigitsCreditCard) {
        this.lastDigitsCreditCard = lastDigitsCreditCard;
    }

    public BigDecimal getValuePaid() {
        return valuePaid;
    }

    public void setValuePaid(BigDecimal valuePaid) {
        this.valuePaid = valuePaid;
    }

    public String getPaymentMessage() {
        return paymentMessage;
    }

    public void setPaymentMessage(String paymentMessage) {
        this.paymentMessage = paymentMessage;
    }

    public boolean isRecurrence() {
        return recurrence;
    }

    public void setRecurrence(boolean recurrence) {
        this.recurrence = recurrence;
    }
}
