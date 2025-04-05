package com.vulpix.api.controller;

import com.vulpix.api.dto.autenticacao.UsuarioTokenDto;
import com.vulpix.api.dto.googleauth.GoogleAuthOtpRequest;
import com.vulpix.api.dto.googleauth.GoogleAuthQRCodeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
            @ApiResponse(responseCode = "409", description = "Autenticação de dois fatores já habilitada.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"status\": 409, \"detail\": \"A autenticação de dois fatores já está habilitada em sua conta.\", \"timestamp\": \"2025-03-17T16:59:50.5115104\" }")
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Falha ao gerar QR Code.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"status\": 500, \"detail\": \"Falha ao gerar QR Code.\", \"timestamp\": \"2025-03-17T16:59:50.5115104\" }")
                    )
            )
    })
    @GetMapping("/google/gerar-qrcode")
    ResponseEntity<GoogleAuthQRCodeResponse> gerarQRCode(@RequestParam @Email @NotBlank String email);

    @Operation(summary = "Verifica se o OTP é válido",
            description = "Verifica se o OTP enviado pelo usuário é válido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verificação realizada com sucesso.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"isOtpValido\":true}")
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Payload inválido.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"status\": 400, \"detail\": \"O OTP informado não é um número válido.\", \"timestamp\": \"2025-03-17T16:59:50.5115104\" }")
                    )
            ),
            @ApiResponse(responseCode = "409", description = "Autenticação de dois fatores não habilitada.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"status\": 409, \"detail\": \"Sua conta não possui a autenticação de dois fatores habilitada.\", \"timestamp\": \"2025-03-17T16:59:50.5115104\" }")
                    )
            )
    })
    @PostMapping("/google/validar-otp")
    ResponseEntity<UsuarioTokenDto> validarOtp(@Valid @RequestBody GoogleAuthOtpRequest googleAuthOtpRequest);

    @Operation(summary = "Desabilita a autenticação de dois fatores",
            description = "Desabilita a autenticação de dois fatores.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Autenticação de dois fatores desabilitada com sucesso.",
                    content = @Content(examples = @ExampleObject())
            ),
            @ApiResponse(responseCode = "401", description = "OTP inválido.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"status\": 401, \"detail\": \"OTP inválido.\", \"timestamp\": \"2025-03-17T16:59:50.5115104\" }")
                    )
            ),
            @ApiResponse(responseCode = "409", description = "Autenticação de dois fatores não habilitada.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"status\": 409, \"detail\": \"Sua conta não possui a autenticação de dois fatores habilitada.\", \"timestamp\": \"2025-03-17T16:59:50.5115104\" }")
                    )
            )
    })
    @DeleteMapping("/google")
    ResponseEntity<Void> desabilitarAutenticacao(
            @RequestHeader String otp,
            @RequestHeader @Email @NotBlank String email
    );
}