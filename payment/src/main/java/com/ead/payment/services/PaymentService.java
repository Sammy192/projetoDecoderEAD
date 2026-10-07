package com.ead.payment.services;

import com.ead.payment.dto.PaymentRequestDTO;
import com.ead.payment.models.PaymentModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public interface PaymentService {

    PaymentModel requestPayment(PaymentRequestDTO paymentRequest, UUID userId);
    Page<PaymentModel> findAllByUser(Specification<PaymentModel> spec, Pageable pageable);
}
