package com.vulpix.api.controller.impl;

import com.vulpix.api.controller.GoogleAuthController;
import com.vulpix.api.dto.googleauth.*;
import com.vulpix.api.service.GoogleAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GoogleAuthControllerImpl implements GoogleAuthController {
    @Autowired
    private GoogleAuthService googleAuthService;

    @Override
    public ResponseEntity<GoogleAuthQRCodeResponse> gerarQRCode(String email) {
        String secret = googleAuthService.getSecret();
        String qrcodeBase64 = googleAuthService.gerarQRCode(secret, email, "Vulpix");
        GoogleAuthQRCodeResponse googleAuthQRCodeDto = GoogleAuthMapper.criaDtoQRCode(qrcodeBase64);
        return ResponseEntity.status(200).body(googleAuthQRCodeDto);
    }

    @Override
    public ResponseEntity<GoogleAuthOtpResponse> validarOtp(GoogleAuthOtpRequest googleAuthOtpRequest) {
        Boolean isOtpValido = googleAuthService.validarOTP(googleAuthOtpRequest.getOtp());
        GoogleAuthOtpResponse googleAuthOtpDto = GoogleAuthMapper.criaDtoOtp(isOtpValido);
        return ResponseEntity.status(200).body(googleAuthOtpDto);
    }
}