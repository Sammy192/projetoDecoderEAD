package com.ead.payment.controllers;

import com.ead.payment.dto.PaymentRequestDTO;
import com.ead.payment.models.PaymentModel;
import com.ead.payment.services.PaymentService;
import com.ead.payment.services.UserService;
import com.ead.payment.specifications.SpecificationTemplate;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class PaymentController {

    private final UserService userService;
    private final PaymentService paymentService;


    public PaymentController(UserService userService, PaymentService paymentService) {
        this.userService = userService;
        this.paymentService = paymentService;
    }

    @PreAuthorize("hasAnyRole('USER')")
    @PostMapping("/{userId}/payments")
    public ResponseEntity<Object> requestPayment(@PathVariable UUID userId,
                                                 @RequestBody @Valid PaymentRequestDTO paymentRequest) {

        PaymentModel paymentModel = paymentService.requestPayment(paymentRequest, userId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(paymentModel);
    }

    @PreAuthorize("hasAnyRole('USER')")
    @GetMapping("/{userId}/payments")
    public ResponseEntity<Page<PaymentModel>> getAllPayments(@PathVariable(value="userId") UUID userId,
                                                             SpecificationTemplate.PaymentSpec spec,
                                                             Pageable pageable){

    return ResponseEntity.status(HttpStatus.OK)
            .body(paymentService.findAllByUser(SpecificationTemplate.paymentUserId(userId).and(spec), pageable));
    }
}
