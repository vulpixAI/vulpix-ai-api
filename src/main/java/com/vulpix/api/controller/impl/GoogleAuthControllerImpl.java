package com.vulpix.api.controller.impl;

import com.vulpix.api.controller.GoogleAuthController;
import com.vulpix.api.dto.googleauth.GoogleAuthMapper;
import com.vulpix.api.dto.googleauth.GoogleAuthOtpDto;
import com.vulpix.api.service.GoogleAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.awt.image.BufferedImage;

@RestController
public class GoogleAuthControllerImpl implements GoogleAuthController {
    @Autowired
    private GoogleAuthService googleAuthService;

    @Override
    public BufferedImage gerarQRCode(@PathVariable String email) {
        String secret = googleAuthService.getSecret();
        return googleAuthService.gerarQRCode(secret, email, "Vulpix");
    }

    @Override
    public ResponseEntity<GoogleAuthOtpDto> validarOtp(String otp) {
        Boolean isOtpValido = googleAuthService.validarOTP(otp);
        GoogleAuthOtpDto googleAuthOtpDto = GoogleAuthMapper.criaDtoOtp(isOtpValido);
        return ResponseEntity.status(200).body(googleAuthOtpDto);
    }
}