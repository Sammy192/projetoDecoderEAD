package com.ead.payment.publishers;

import com.ead.payment.dto.PaymentEventDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public PaymentEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Value(value = "${ead.broker.exchange.paymentEventExchange}")
    private String exchangePaymentEvent;

    public void publishPaymentEvent(PaymentEventDTO paymentEventDTO) {
        rabbitTemplate.convertAndSend(exchangePaymentEvent, "", paymentEventDTO);
    }
}
