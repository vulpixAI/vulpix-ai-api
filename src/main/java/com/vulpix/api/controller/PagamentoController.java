package com.vulpix.api.controller;

import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.vulpix.api.dto.Pagamento.PagamentoStatusDto;
import com.vulpix.api.entity.Empresa;
import com.vulpix.api.service.EmpresaService;
import com.vulpix.api.service.PagamentoService;
import com.vulpix.api.service.usuario.autenticacao.UsuarioAutenticadoUtil;
import com.vulpix.api.service.usuario.UsuarioService;
import com.vulpix.api.utils.enums.StatusUsuario;
import com.vulpix.api.utils.helpers.EmpresaHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/pagamentos")
@Tag(name = "Pagamento", description = " Controller para gerenciamento e processamento de pagamentos.")
public class PagamentoController {
    @Autowired
    UsuarioAutenticadoUtil usuarioAutenticadoUtil;

    @Autowired
    EmpresaHelper empresaHelper;

    @Autowired
    PagamentoService pagamentoService;

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
                                            name = "Exemplo de resposta",
                                            value = """
                                                        {
                                                            "url": "https://checkout.stripe.com/pay/cs_test_ABC123XYZ"
                                                        }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "204", description = "Empresa não encontrada.")
            }
    )
    @PostMapping
    public ResponseEntity<String> pagamento() {
        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String emailUsuario = userDetails.getUsername();
        Empresa empresa = empresaHelper.buscarEmpresaPeloUsuario(emailUsuario);

        String url = pagamentoService.criarPaymentLink(empresa);
        return ResponseEntity.status(200).body(url);
    }

    @Value("${stripe.chave-webhook}")
    private String endpointSecret;

    @Operation(
            summary = "Processa eventos do webhook Stripe",
            description = "Recebe eventos do webhook Stripe e atualiza o status dos pagamentos com base no evento recebido.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Evento processado com sucesso."),
                    @ApiResponse(responseCode = "400", description = "Erro ao processar o evento.")
            }
    )
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        pagamentoService.criarWebhook(payload, sigHeader, endpointSecret);
        return ResponseEntity.status(200).build();
    }
}