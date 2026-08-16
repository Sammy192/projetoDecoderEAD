package com.ead.authuser.dto;

import jakarta.validation.constraints.NotBlank;

public record JwtDTO(@NotBlank String token,
                     String type) {

    public JwtDTO(@NotBlank String token) {
        this(token, "Bearer");
    }
}
