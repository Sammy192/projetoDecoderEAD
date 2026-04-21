package com.ead.authuser.dto;

public record UserDTORequest(String username,
                             String email,
                             String password,
                             String oldPassword,
                             String fullName,
                             String phoneNumber,
                             String imageUrl) {
}
