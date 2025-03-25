package com.vulpix.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.awt.image.BufferedImage;

@RequestMapping("/autenticacao")
public interface GoogleAuthController {
    @GetMapping("/gerar-qr/{email}")
    BufferedImage gerarQRCode(@PathVariable String email);

    @GetMapping("/validar-otp")
    boolean validarOtp(String code);
}