package com.vulpix.api.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.vulpix.api.entity.Usuario;
import com.vulpix.api.exception.exceptions.ErroInternoException;
import com.vulpix.api.service.usuario.UsuarioService;
import com.vulpix.api.service.usuario.autenticacao.UsuarioAutenticadoUtil;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class GoogleAuthService {
    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioAutenticadoUtil usuarioAutenticadoUtil;

    private final GoogleAuthenticator googleAuthenticator;

    public GoogleAuthService() {
        googleAuthenticator = new GoogleAuthenticator();
    }

    public Boolean validarOTP(String otp) {
        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String email = userDetails.getUsername();
        Usuario usuario = usuarioService.buscarUsuarioPorEmail(email);
        String secretKey = usuario.getSecretKey();
        return googleAuthenticator.authorize(secretKey, Integer.parseInt(otp));
    }

    private String gerarSecretKey() {
        return googleAuthenticator.createCredentials().getKey();
    }

    private String getSecretKeyPorEmailUsuario(String email) {
        Usuario usuario = usuarioService.buscarUsuarioPorEmail(email);
        String secretKey = usuario.getSecretKey();

        if (secretKey == null) {
            secretKey = gerarSecretKey();
            usuarioService.cadastrarSecretKey(secretKey, usuario);
        }

        return secretKey;
    }

    private String converterBufferedImageParaBase64(BufferedImage imagem) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            ImageIO.write(imagem, "PNG", baos);
        } catch (Exception e) {
            throw new ErroInternoException("Falha ao realizar conversão do QR Code para base64.");
        }

        byte[] imagemBytes = baos.toByteArray();

        return Base64.getEncoder().encodeToString(imagemBytes);
    }

    public String gerarQRCode() {
        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String email = userDetails.getUsername();
        String secretKey = getSecretKeyPorEmailUsuario(email);
        String issuer = "vulpix.AI";

        String uri = String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s", issuer, email, secretKey, issuer);

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.MARGIN, 1);

        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(uri, BarcodeFormat.QR_CODE, 200, 200, hints);

            BufferedImage image = new BufferedImage(bitMatrix.getWidth(), bitMatrix.getHeight(), BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < bitMatrix.getWidth(); x++) {
                for (int y = 0; y < bitMatrix.getHeight(); y++) {
                    image.setRGB(x, y, bitMatrix.get(x, y) ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
                }
            }

            return converterBufferedImageParaBase64(image);
        } catch (Exception e) {
            throw new ErroInternoException("Falha ao gerar QR Code.");
        }
    }
}