package com.vulpix.api.controller;

import com.vulpix.api.dto.googleauth.GoogleAuthOtpRequest;
import com.vulpix.api.dto.googleauth.GoogleAuthOtpResponse;
import com.vulpix.api.dto.googleauth.GoogleAuthQRCodeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/autenticacoes")
@Tag(name = "Autenticação")
public interface GoogleAuthController {
    @Operation(summary = "Realiza a geração de um QR Code",
            description = "Realiza a geração de um QR Code para a autenticação do usuário.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "QR Code gerado com sucesso.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"secretKey\": \"string\", \"qrcodeBase64\": \"string\"}")
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Falha ao gerar QR Code.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"status\": 500, \"detail\": \"Falha ao gerar QR Code.\", \"timestamp\": \"2025-03-17T16:59:50.5115104\" }")
                    )
            )
    })
    @GetMapping("/gerar-qrcode")
    ResponseEntity<GoogleAuthQRCodeResponse> gerarQRCode();

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
    @PostMapping("/validar-otp")
    ResponseEntity<GoogleAuthOtpResponse> validarOtp(@RequestBody GoogleAuthOtpRequest googleAuthOtpRequest);
}