package com.vulpix.api.Controller;

import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.vulpix.api.Dto.Pagamento.PagamentoStatusDto;
import com.vulpix.api.Entity.Empresa;
import com.vulpix.api.Repository.EmpresaRepository;
import com.vulpix.api.Service.EmpresaService;
import com.vulpix.api.Service.PagamentoService;
import com.vulpix.api.Service.Usuario.Autenticacao.UsuarioAutenticadoUtil;
import com.vulpix.api.Service.Usuario.UsuarioService;
import com.vulpix.api.Utils.Enum.StatusUsuario;
import com.vulpix.api.Utils.Helpers.EmpresaHelper;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/pagamentos")
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
