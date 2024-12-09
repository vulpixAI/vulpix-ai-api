package com.vulpix.api.Controller;

import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.vulpix.api.Dto.Pagamento.PagamentoStatusDto;
import com.vulpix.api.Entity.Empresa;
import com.vulpix.api.Service.EmpresaService;
import com.vulpix.api.Service.PagamentoService;
import com.vulpix.api.Service.Usuario.Autenticacao.UsuarioAutenticadoUtil;
import com.vulpix.api.Service.Usuario.UsuarioService;
import com.vulpix.api.Utils.Enum.StatusUsuario;
import com.vulpix.api.Utils.Helpers.EmpresaHelper;
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
    @Autowired
    EmpresaService empresaService;
    @Autowired
    UsuarioService usuarioService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

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
    public ResponseEntity<String> pagamento() throws Exception {
        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String emailUsuario = userDetails.getUsername();
        Empresa empresa = empresaHelper.buscarEmpresaPeloUsuario(emailUsuario);

        if (empresa == null) return ResponseEntity.status(204).build();

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
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
            String status = null;
            String empresaNome = null;

            switch (event.getType()) {
                case "checkout.session.completed", "payment_intent.succeeded", "invoice.payment_succeeded":
                    System.out.println("Evento: " + event.getType());
                    status = processarEvento(event, StatusUsuario.AGUARDANDO_FORMULARIO);
                    break;
                case "payment_intent.payment_failed", "invoice.payment_failed", "charge.failed":
                    System.out.println("Evento: " + event.getType());
                    status = processarEvento(event, null);
                    break;
                default:
                    break;
            }

            messagingTemplate.convertAndSend("/topic/status-payment",
                    PagamentoStatusDto.builder().status(status).empresaNome(empresaNome).build());

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Operation(
            summary = "Processa evento de pagamento",
            description = "Lógica interna para processar eventos recebidos do webhook Stripe. Atualiza o status da empresa conforme o evento.",
            hidden = true
    )
    private String processarEvento(Event event, StatusUsuario novoStatus) {
        Session session = (Session) event.getData().getObject();
        Map<String, String> metadata = session.getMetadata();
        String empresaIdString = metadata.get("empresa_id");
        if (empresaIdString != null) {
            UUID empresaId = UUID.fromString(empresaIdString);
            Empresa empresa = empresaService.buscaPorId(empresaId);

            if (empresa != null) {
                String empresaNome = empresa.getNomeFantasia();
                System.out.println("Evento processado para a empresa: " + empresaNome);

                if (novoStatus != null) {
                    usuarioService.atualizaStatus(empresa, novoStatus);
                }

                return event.getType().equals("checkout.session.completed") ? "sucesso" : "falhou";
            } else {
                System.out.println("Empresa não encontrada com o ID: " + empresaId);
            }
        } else {
            System.out.println("Metadata 'empresa_id' não encontrado.");
        }

        return "falhou";
    }
}
