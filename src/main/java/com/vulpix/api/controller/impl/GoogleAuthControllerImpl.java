package com.vulpix.api.controller.impl;

import com.vulpix.api.controller.GoogleAuthController;
import com.vulpix.api.dto.autenticacao.UsuarioTokenDto;
import com.vulpix.api.dto.googleauth.GoogleAuthOtpRequest;
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
    public ResponseEntity<GoogleAuthQRCodeResponse> gerarQRCode(String email) {
        GoogleAuthQRCodeResponse googleAuthQRCodeResponse = googleAuthService.gerarQRCode(email);
        return ResponseEntity.status(200).body(googleAuthQRCodeResponse);
    }

    @Override
    public ResponseEntity<UsuarioTokenDto> validarOtp(GoogleAuthOtpRequest dto) {
        UsuarioTokenDto googleAuthOtpResponse = googleAuthService.validarOTP(dto.getOtp(), dto.getSecretKey(), dto.getEmail(), dto.getDispositivoCode());
        return ResponseEntity.status(200).body(googleAuthOtpResponse);
    }

    @Override
    public ResponseEntity<Void> desabilitarAutenticacao(String otp, String email) {
        googleAuthService.desabilitarAutenticacao(otp, email);
        return ResponseEntity.status(204).build();
    }
}