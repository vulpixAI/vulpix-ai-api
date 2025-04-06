package com.vulpix.api.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.vulpix.api.config.security.jwt.GerenciadorTokenJwt;
import com.vulpix.api.dto.autenticacao.MfaLoginDto;
import com.vulpix.api.dto.autenticacao.UsuarioTokenDto;
import com.vulpix.api.dto.googleauth.GoogleAuthQRCodeResponse;
import com.vulpix.api.dto.usuario.UsuarioMapper;
import com.vulpix.api.entity.Usuario;
import com.vulpix.api.exception.exceptions.ConflitoException;
import com.vulpix.api.exception.exceptions.ErroInternoException;
import com.vulpix.api.exception.exceptions.NaoAutorizadoException;
import com.vulpix.api.exception.exceptions.RequisicaoInvalidaException;
import com.vulpix.api.service.usuario.autenticacao.UsuarioAutenticadoUtil;
import com.vulpix.api.utils.helpers.UsuarioHelper;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
    private UsuarioHelper usuarioHelper;

    @Autowired
    private UsuarioAutenticadoUtil usuarioAutenticadoUtil;

    @Autowired
    private GerenciadorTokenJwt gerenciadorTokenJwt;

    private final GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();

    public Integer converterOtpParaInteger(String otp) {
        try {
            return Integer.parseInt(otp);
        } catch (NumberFormatException e) {
            throw new RequisicaoInvalidaException("O OTP informado não é um número válido.");
        }
    }

    public UsuarioTokenDto validarOTP(String otp, String secretKey, String email, String dispositivoCode) {
        Integer valorOtp = converterOtpParaInteger(otp);
        Usuario usuario = usuarioHelper.buscaUsuarioPorEmail(email);
        String secretKeyCadastrada = usuario.getSecretKey();

        if (secretKeyCadastrada == null && secretKey == null) {
            throw new ConflitoException("Sua conta não possui a autenticação de dois fatores habilitada.");
        }

        if (secretKeyCadastrada == null) secretKeyCadastrada = secretKey;

        boolean isOtpValido = googleAuthenticator.authorize(secretKeyCadastrada, valorOtp);

        if (!isOtpValido) {
            throw new NaoAutorizadoException("OTP inválido.");
        }

        if (usuario.getSecretKey() == null) {
            usuarioHelper.cadastrarSecretKey(secretKey, usuario);
        }

        if (dispositivoCode != null) {
            usuarioHelper.marcarDispositivoComoConfiavel(usuario, dispositivoCode);
        }

        Authentication auth = new UsernamePasswordAuthenticationToken(usuario.getEmail(), usuario.getSenha());
        String token = gerenciadorTokenJwt.generateToken(auth);

        return UsuarioMapper.retornaUsuario(usuario, token, usuario.getSecretKey());
    }

    private String gerarSecretKey() {
        return googleAuthenticator.createCredentials().getKey();
    }

    private void verificarExistenciaSecretKeyPorEmail(String email) {
        Usuario usuario = usuarioHelper.buscaUsuarioPorEmail(email);
        if (usuario.getSecretKey() != null) {
            throw new ConflitoException("A autenticação de dois fatores já está habilitada em sua conta.");
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

    public GoogleAuthQRCodeResponse gerarQRCode(String email) {
        verificarExistenciaSecretKeyPorEmail(email);

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

    public void desabilitarAutenticacao(String otp, String email) {
        Integer valorOtp = converterOtpParaInteger(otp);
        Usuario usuario = usuarioHelper.buscaUsuarioPorEmail(email);
        String secretKey = usuario.getSecretKey();

        if (secretKey == null) {
            throw new ConflitoException("Sua conta não possui a autenticação de dois fatores habilitada.");
        }

        boolean isOtpValido = googleAuthenticator.authorize(secretKey, valorOtp);

        if (!isOtpValido) {
            throw new NaoAutorizadoException("OTP inválido.");
        }

        usuarioHelper.desabilitarAutenticacao(usuario);
    }

    public UsuarioTokenDto autenticarComMfa(MfaLoginDto mfaLoginDto) {
        UsuarioTokenDto response = validarOTP(mfaLoginDto.getOtp(), mfaLoginDto.getSecretKey(), mfaLoginDto.getEmail(), mfaLoginDto.getDispositivoCode());

        Usuario usuario = usuarioHelper.buscaUsuarioPorEmail(mfaLoginDto.getEmail());

        usuarioHelper.marcarDispositivoComoConfiavel(usuario, mfaLoginDto.getDispositivoCode());

        Authentication auth = new UsernamePasswordAuthenticationToken(usuario.getEmail(), usuario.getSenha());
        String token = gerenciadorTokenJwt.generateToken(auth);

        return UsuarioMapper.retornaUsuario(usuario, token, usuario.getSecretKey());
    }
}