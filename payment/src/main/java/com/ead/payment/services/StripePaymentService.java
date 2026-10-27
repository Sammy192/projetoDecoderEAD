package com.ead.payment.services;

import com.ead.payment.models.CreditCardModel;
import com.ead.payment.models.PaymentModel;

public interface StripePaymentService {
    PaymentModel processStripePayment(PaymentModel paymentModel, CreditCardModel creditCardModel);
}
