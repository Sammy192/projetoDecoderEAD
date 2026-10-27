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
@ConditionalOnProperty(name = "integration.stripe.enabled", havingValue = "false", matchIfMissing = true)
public class StripePaymentServiceMock implements StripePaymentService {

    private Logger logger = LogManager.getLogger(StripePaymentServiceMock.class);

    @Override
    public PaymentModel processStripePayment(PaymentModel paymentModel, CreditCardModel creditCardModel) {
        logger.warn("Serviço de pagamento Stripe Mock utilizado.");
        paymentModel.setPaymentControl(PaymentStatusControlEnum.APPROVED);
        paymentModel.setPaymentMessage("Serviço de pagamento Stripe Mock utilizado.");
        paymentModel.setPaymentCompletionDate(LocalDateTime.now(ZoneId.of("UTC")));
        return paymentModel;
    }

}
