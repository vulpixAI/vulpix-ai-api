package com.vulpix.api.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

@Service
public class GoogleAuthService {
    private final GoogleAuthenticator googleAuthenticator;
    private final GoogleAuthenticatorKey key;

    public GoogleAuthService() {
        googleAuthenticator = new GoogleAuthenticator();
        this.key = googleAuthenticator.createCredentials();
    }

    public String getSecret() {
        return key.getKey();
    }

    public int gerarOTP() {
        long timestamp = System.currentTimeMillis();
        return googleAuthenticator.getTotpPassword(key.getKey(), timestamp);
    }

    public boolean validarOTP(String otp) {
        return googleAuthenticator.authorize(key.getKey(), Integer.parseInt(otp));
    }

    public BufferedImage gerarQRCode(String secret, String email, String issuer) throws Exception{
        String uri = String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s", issuer, email, secret, issuer);

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.MARGIN, 1);

        BitMatrix bitMatrix = new MultiFormatWriter().encode(uri, BarcodeFormat.QR_CODE, 200, 200, hints);

        BufferedImage image = new BufferedImage(bitMatrix.getWidth(), bitMatrix.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < bitMatrix.getWidth(); x++) {
            for (int y = 0; y < bitMatrix.getHeight(); y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
            }
        }
        return image;
    }
}
