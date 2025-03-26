package com.vulpix.api.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.vulpix.api.dto.googleauth.GoogleAuthOtpResponse;
import com.vulpix.api.dto.googleauth.GoogleAuthQRCodeResponse;
import com.vulpix.api.entity.Usuario;
import com.vulpix.api.exception.exceptions.ConflitoException;
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

    public GoogleAuthOtpResponse validarOTP(String otp, String secretKey) {
        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String email = userDetails.getUsername();
        Usuario usuario = usuarioService.buscarUsuarioPorEmail(email);
        String secretKeyCadastrada = usuario.getSecretKey();

        if (secretKeyCadastrada == null) {
            secretKeyCadastrada = secretKey;
        }

        boolean isOtpValido = googleAuthenticator.authorize(secretKeyCadastrada, Integer.parseInt(otp));

        if (isOtpValido && usuario.getSecretKey() == null) {
            usuarioService.cadastrarSecretKey(secretKey, usuario);
        }

        return new GoogleAuthOtpResponse(isOtpValido);
    }

    private String gerarSecretKey() {
        return googleAuthenticator.createCredentials().getKey();
    }

    private void verificaExistenciaSecretKeyPorEmail(String email) {
        Usuario usuario = usuarioService.buscarUsuarioPorEmail(email);
        if (usuario.getSecretKey() != null) {
            throw new ConflitoException("A autenticação de dois fatores já está ativada para sua conta.");
        }
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

    public GoogleAuthQRCodeResponse gerarQRCode() {
        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String email = userDetails.getUsername();

        verificaExistenciaSecretKeyPorEmail(email);

        String secretKey = gerarSecretKey();
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

            String qrcodeBase64 = converterBufferedImageParaBase64(image);

            return new GoogleAuthQRCodeResponse(secretKey, qrcodeBase64);
        } catch (Exception e) {
            throw new ErroInternoException("Falha ao gerar QR Code.");
        }
    }
}