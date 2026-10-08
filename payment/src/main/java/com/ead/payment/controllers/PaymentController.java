package com.ead.payment.controllers;

import com.ead.payment.configs.security.AuthenticationCurrentUserService;
import com.ead.payment.configs.security.UserDetailsImpl;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class PaymentController {

    private final UserService userService;
    private final PaymentService paymentService;
    private final AuthenticationCurrentUserService authenticationCurrentUserService;


    public PaymentController(UserService userService, PaymentService paymentService, AuthenticationCurrentUserService authenticationCurrentUserService) {
        this.userService = userService;
        this.paymentService = paymentService;
        this.authenticationCurrentUserService = authenticationCurrentUserService;
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
                                                             Pageable pageable) {

        UserDetailsImpl userDetails = authenticationCurrentUserService.getCurrentUser();
        if (userDetails.getUserId().equals(userId) || userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(paymentService.findAllByUser(SpecificationTemplate.paymentUserId(userId).and(spec), pageable));
        } else {
            throw new AccessDeniedException("Forbidden");
        }
    }

    @PreAuthorize("hasAnyRole('USER')")
    @GetMapping("/{userId}/payments/{paymentId}")
    public ResponseEntity<Object> getPaymentById(@PathVariable(value="userId") UUID userId,
                                                @PathVariable(value="paymentId") UUID paymentId){
        UserDetailsImpl userDetails = authenticationCurrentUserService.getCurrentUser();
        if(userDetails.getUserId().equals(userId) || userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(paymentService.findPaymentByUser(userId, paymentId));
        } else {
            throw new AccessDeniedException("Forbidden");
        }
    }
}
