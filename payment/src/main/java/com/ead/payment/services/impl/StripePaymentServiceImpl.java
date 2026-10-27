package com.ead.payment.services.impl;

import com.ead.payment.enums.PaymentStatusControlEnum;
import com.ead.payment.models.CreditCardModel;
import com.ead.payment.models.PaymentModel;
import com.ead.payment.services.StripePaymentService;
import com.stripe.Stripe;
import com.stripe.exception.CardException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentConfirmParams;
import com.stripe.param.PaymentIntentCreateParams;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@ConditionalOnProperty(name = "integration.stripe.enabled", havingValue = "true")
public class StripePaymentServiceImpl implements StripePaymentService {

    private Logger logger = LogManager.getLogger(StripePaymentServiceImpl.class);

    @Value(value = "${integration.stripe.secretKey}")
    private String secretKey;


    @Override
    public PaymentModel processStripePayment(PaymentModel paymentModel, CreditCardModel creditCardModel) {
        Stripe.apiKey = secretKey;
        String paymentIntentId = null;
        try {
            PaymentIntentCreateParams paramsPaymentIntent = PaymentIntentCreateParams.builder()
                    .setAmount(paymentModel.getValuePaid().multiply(new BigDecimal("100")).longValue())
                    .setCurrency("brl")
                    .setPaymentMethod(getPaymentMethod(creditCardModel.getCreditCardNumber().replace(" ", "")))
                    .addPaymentMethodType("card")
                    .build();
            PaymentIntent paymentIntent = PaymentIntent.create(paramsPaymentIntent);
            paymentIntentId = paymentIntent.getId();

            PaymentIntentConfirmParams paramsPaymentConfirm = PaymentIntentConfirmParams.builder().build();
            PaymentIntent confirmedPaymentIntent = paymentIntent.confirm(paramsPaymentConfirm);

            if ("succeeded".equalsIgnoreCase(confirmedPaymentIntent.getStatus())) {
                handleSuccessfulPayment(paymentModel, confirmedPaymentIntent);
            } else {
                handleFailedConfirmation(paymentModel, confirmedPaymentIntent);
            }
        } catch (CardException cardException) {
            handleCardException(cardException, paymentModel, paymentIntentId);
        } catch (Exception e) {
            handleGenericError(e, paymentModel, paymentIntentId);
        }
        return paymentModel;
    }

    private String getPaymentMethod(String creditCardNumber) {
        return switch (creditCardNumber) {
            case "4242424242424242" -> "pm_card_visa";
            case "5555555555554444" -> "pm_card_mastercard";
            case "4000000000009995" -> "pm_card_visa_chargeDeclinedInsufficientFunds";
            case "4000000000000127" -> "pm_card_chargeDeclinedIncorrectCvc";
            default -> "pm_card_visa_chargeDeclined";
        };
    }

    private void handleSuccessfulPayment(PaymentModel paymentModel, PaymentIntent paymentIntent) {
        paymentModel.setPaymentControl(PaymentStatusControlEnum.APPROVED);
        paymentModel.setPaymentMessage("Payment approved - PaymentIntentId: " + paymentIntent.getId());
        paymentModel.setPaymentCompletionDate(LocalDateTime.now(ZoneId.of("UTC")));
    }

    private void handleFailedConfirmation(PaymentModel paymentModel, PaymentIntent paymentIntent) {
        logger.error("Error confirming payment: paymentIntentId: {}", paymentIntent.getId());
        paymentModel.setPaymentControl(PaymentStatusControlEnum.ERROR);
        String message = "Payment confirmation failed.";
        if (paymentIntent.getLastPaymentError() != null) {
            message += String.format(" Cause: %s, Message: %s.",
                    paymentIntent.getLastPaymentError().getCode(),
                    paymentIntent.getLastPaymentError().getMessage());
        }
        paymentModel.setPaymentMessage(message + " PaymentIntentId: " + paymentIntent.getId());
    }

    private void handleCardException(CardException cardException, PaymentModel paymentModel, String paymentIntentId) {
        logger.error("A card declined occurred: {} ", cardException.getMessage());
        paymentModel.setPaymentControl(PaymentStatusControlEnum.DECLINED);
        String message = (paymentIntentId != null)
                ? String.format("Payment declined. PaymentIntentId: %s, Cause: %s, Message: %s.",
                paymentIntentId, cardException.getCode(), cardException.getMessage())
                : String.format("Payment declined during intent creation. Cause: %s, Message: %s.",
                cardException.getCode(), cardException.getMessage());
        paymentModel.setPaymentMessage(message);
    }

    private void handleGenericError(Exception exception, PaymentModel paymentModel, String paymentIntentId) {
        logger.error("A generic error occurred during payment processing. Error: {} ", exception.getMessage(), exception);
        paymentModel.setPaymentControl(PaymentStatusControlEnum.ERROR);
        paymentModel.setPaymentMessage("An unexpected error occurred during payment processing." +
                (paymentIntentId != null ? " Associated PaymentIntentId: " + paymentIntentId : ""));
    }
}
