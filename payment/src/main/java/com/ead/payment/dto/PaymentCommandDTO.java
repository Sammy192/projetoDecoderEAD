package com.ead.payment.dto;

import java.util.UUID;

public record PaymentCommandDTO(UUID userId,
                                UUID paymentId,
                                UUID cardId) {
}
