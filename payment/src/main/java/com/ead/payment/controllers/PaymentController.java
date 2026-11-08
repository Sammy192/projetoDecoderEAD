package com.ead.payment.controllers;

import com.ead.payment.configs.security.AuthenticationCurrentUserService;
import com.ead.payment.configs.security.UserDetailsImpl;
import com.ead.payment.dto.PaymentRequestDTO;
import com.ead.payment.dto.PaymentResponseDTO;
import com.ead.payment.models.PaymentModel;
import com.ead.payment.services.PaymentService;
import com.ead.payment.specifications.SpecificationTemplate;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
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
@Tag(name = "Payment", description = "Endpoints for managing user payments")
public class PaymentController {

    private static final Logger logger = LogManager.getLogger(PaymentController.class);

    private final PaymentService paymentService;
    private final AuthenticationCurrentUserService authenticationCurrentUserService;


    public PaymentController(PaymentService paymentService, AuthenticationCurrentUserService authenticationCurrentUserService) {
        this.paymentService = paymentService;
        this.authenticationCurrentUserService = authenticationCurrentUserService;
    }

    @Operation(summary = "Request a new payment", description = "Creates a new payment request for a user. This initiates the payment process.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Payment request created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping("/{userId}/payments")
    public ResponseEntity<PaymentResponseDTO> requestPayment(@PathVariable UUID userId,
                                                 @RequestBody @Valid PaymentRequestDTO paymentRequest) {
        logger.debug("POST requestPayment userId: {}", userId);

        PaymentModel paymentModel = paymentService.requestPayment(paymentRequest, userId);

        logger.debug("Payment request created successfully with paymentId: {}", paymentModel.getPaymentId());
        logger.info("Payment request created successfully for userId: {}", userId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new PaymentResponseDTO(paymentModel));
    }

    @Operation(summary = "Get all payments for a user",
            description = "Returns a paginated list of payments for a specific user. Can be filtered by payment details.",
            parameters = {
                    @Parameter(name = "paymentControl", description = "Filtra pelo status do pagamento.", in = ParameterIn.QUERY, schema = @Schema(type = "string", allowableValues = {"PENDING", "APPROVED", "DECLINED", "ERROR"})),
                    @Parameter(name = "valuePaid", description = "Filtra pelo valor exato pago.", in = ParameterIn.QUERY, schema = @Schema(type = "number", format = "double")),
                    @Parameter(name = "lastDigitsCreditCard", description = "Filtra pelos últimos dígitos do cartão de crédito (busca parcial, case-insensitive).", in = ParameterIn.QUERY, schema = @Schema(type = "string")),
                    @Parameter(name = "paymentMessage", description = "Filtra pela mensagem de status do pagamento (busca parcial, case-insensitive).", in = ParameterIn.QUERY, schema = @Schema(type = "string"))
            })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of payments returned successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized")
    })
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{userId}/payments")
    public ResponseEntity<Page<PaymentResponseDTO>> getAllPayments(@PathVariable UUID userId,
                                                                   @ParameterObject SpecificationTemplate.PaymentSpec spec,
                                                                   @ParameterObject Pageable pageable) {
        checkAuthorization(userId);
        Page<PaymentModel> paymentModelPage = paymentService.findAllByUser(SpecificationTemplate.paymentUserId(userId).and(spec), pageable);
        return ResponseEntity.status(HttpStatus.OK)
                .body(paymentModelPage.map(PaymentResponseDTO::new));
    }

    @Operation(summary = "Get a specific payment for a user", description = "Returns details of a single payment by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment details returned successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized"),
            @ApiResponse(responseCode = "404", description = "User or Payment not found")
    })
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{userId}/payments/{paymentId}")
    public ResponseEntity<PaymentResponseDTO> getPaymentById(@PathVariable UUID userId,
                                                             @PathVariable UUID paymentId){
        checkAuthorization(userId);
        PaymentModel paymentModel = paymentService.findPaymentByUser(userId, paymentId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new PaymentResponseDTO(paymentModel));
    }

    private void checkAuthorization(UUID userId) {
        UserDetailsImpl userDetails = authenticationCurrentUserService.getCurrentUser();
        boolean isAdmin = userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        if (!userDetails.getUserId().equals(userId) && !isAdmin) {
            throw new AccessDeniedException("Forbidden");
        }
    }
}
