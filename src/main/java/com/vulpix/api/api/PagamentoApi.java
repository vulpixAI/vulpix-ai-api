package com.vulpix.api.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/pagamentos")
@Tag(name = "Pagamento", description = " Controller para gerenciamento e processamento de pagamentos.")
public interface PagamentoApi {
    @Operation(
            summary = "Cria um link de pagamento",
            description = "Gera um link de pagamento para a empresa autenticada. Retorna a URL do pagamento.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Link de pagamento gerado com sucesso.",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                        {
                                                            "url": "https://checkout.stripe.com/pay/cs_test_ABC123XYZ"
                                                        }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "204", description = "Empresa não encontrada.", content = @Content(examples = @ExampleObject()))
            }
    )
    @PostMapping
    ResponseEntity<String> pagamento();

    @Operation(
            summary = "Processa eventos do webhook Stripe",
            description = "Recebe eventos do webhook Stripe e atualiza o status dos pagamentos com base no evento recebido.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Evento processado com sucesso.", content = @Content(examples = @ExampleObject())),
                    @ApiResponse(responseCode = "400", description = "Erro ao processar evento.",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(value = "{ \"status\": 400, \"detail\": \"Erro ao processar evento.\", \"timestamp\": \"2025-03-17T16:59:50.5115104\" }")
                            )
                    )
            }
    )
    @PostMapping("/webhook")
    ResponseEntity<String> handleWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader);
}