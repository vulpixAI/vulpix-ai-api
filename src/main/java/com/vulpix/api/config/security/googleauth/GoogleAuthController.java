package com.vulpix.api.config.security.googleauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.awt.image.BufferedImage;

@RestController
public class GoogleAuthController {

    @Autowired
    private GoogleAuthService googleAuthService;

    @Autowired
    private QRCodeGenerator qrCodeGenerator;

    @GetMapping("/generate-qr/{email}")
    public BufferedImage generateQRCode(@PathVariable String email) throws Exception {
        String secret = googleAuthService.getSecret();
        return qrCodeGenerator.generateQRCode(secret, email, "Vulpix");
    }

    @GetMapping("/validate-otp")
    public boolean validadeOtp(String code) {
        return googleAuthService.validateOTP(code);
    }

}
