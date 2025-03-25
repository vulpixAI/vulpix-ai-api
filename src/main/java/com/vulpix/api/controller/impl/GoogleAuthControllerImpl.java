package com.vulpix.api.controller.impl;

import com.vulpix.api.controller.GoogleAuthController;
import com.vulpix.api.exception.exceptions.ErroInternoException;
import com.vulpix.api.service.GoogleAuthService;
import org.springframework.beans.factory.annotation.Autowired;
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
        try {
            return googleAuthService.gerarQRCode(secret, email, "Vulpix");
        } catch (Exception e) {
            throw new ErroInternoException("Falha ao gerar QR Code.");
        }
    }

    @Override
    public boolean validarOtp(String code) {
        return googleAuthService.validarOTP(code);
    }
}