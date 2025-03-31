package com.vulpix.api.controller.impl;

import com.vulpix.api.controller.GoogleAuthController;
import com.vulpix.api.dto.googleauth.GoogleAuthOtpRequest;
import com.vulpix.api.dto.googleauth.GoogleAuthOtpResponse;
import com.vulpix.api.dto.googleauth.GoogleAuthQRCodeResponse;
import com.vulpix.api.service.GoogleAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GoogleAuthControllerImpl implements GoogleAuthController {
    @Autowired
    private GoogleAuthService googleAuthService;

    @Override
    public ResponseEntity<GoogleAuthQRCodeResponse> gerarQRCode() {
        GoogleAuthQRCodeResponse googleAuthQRCodeResponse = googleAuthService.gerarQRCode();
        return ResponseEntity.status(200).body(googleAuthQRCodeResponse);
    }

    @Override
    public ResponseEntity<GoogleAuthOtpResponse> validarOtp(GoogleAuthOtpRequest dto) {
        GoogleAuthOtpResponse googleAuthOtpResponse = googleAuthService.validarOTP(dto.getOtp(), dto.getSecretKey());
        return ResponseEntity.status(200).body(googleAuthOtpResponse);
    }

    @Override
    public ResponseEntity<Void> desabilitarAutenticacao(String otp) {
        googleAuthService.desabilitarAutenticacao(otp);
        return ResponseEntity.status(204).build();
    }
}