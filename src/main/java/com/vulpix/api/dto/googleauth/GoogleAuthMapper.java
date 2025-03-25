package com.vulpix.api.dto.googleauth;

public class GoogleAuthMapper {
    public static GoogleAuthOtpDto criaDtoOtp(Boolean isOtpValido) {
        if (isOtpValido == null) return null;

        GoogleAuthOtpDto googleAuthOtpDto = GoogleAuthOtpDto.builder()
                .isOtpValido(isOtpValido)
                .build();

        return googleAuthOtpDto;
    }
}