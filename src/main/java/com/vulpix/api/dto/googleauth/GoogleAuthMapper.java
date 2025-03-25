package com.vulpix.api.dto.googleauth;

public class GoogleAuthMapper {
    public static GoogleAuthOtpResponse criaDtoOtp(Boolean isOtpValido) {
        if (isOtpValido == null) return null;

        GoogleAuthOtpResponse googleAuthOtpDto = GoogleAuthOtpResponse.builder()
                .isOtpValido(isOtpValido)
                .build();

        return googleAuthOtpDto;
    }

    public static GoogleAuthQRCodeResponse criaDtoQRCode(String qrcodebase64) {
        if (qrcodebase64 == null) return null;

        GoogleAuthQRCodeResponse googleAuthQRCodeDto = GoogleAuthQRCodeResponse.builder()
                .qrcodeBase64(qrcodebase64)
                .build();

        return googleAuthQRCodeDto;
    }
}