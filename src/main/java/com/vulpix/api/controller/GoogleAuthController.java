package com.vulpix.api.controller;

import com.vulpix.api.dto.googleauth.GoogleAuthOtpDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.awt.image.BufferedImage;

@RequestMapping("/autenticacao")
@Tag(name = "Google Auth")
public interface GoogleAuthController {
    @GetMapping("/gerar-qr/{email}")
    BufferedImage gerarQRCode(@PathVariable String email);

    @Operation(summary = "Verifica se o OTP é válido",
            description = "Verifica se o OTP enviado pelo usuário é válido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verificação realizada com sucesso.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"isOtpValido\":true}")
                    )
            )
    })
    @GetMapping("/validar-otp")
    ResponseEntity<GoogleAuthOtpDto> validarOtp(@RequestParam String otp);
}