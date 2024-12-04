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
                case "checkout.session.completed":
                    Session session = (Session) event.getData().getObject();
                    System.out.println("Pagamento completado para o cliente " + session.getCustomer());

                    Map<String, String> metadata = session.getMetadata();

                    String empresaIdString = metadata.get("empresa_id");

                    if (empresaIdString != null) {
                        UUID empresaId = UUID.fromString(empresaIdString);
                        Empresa empresa = empresaService.buscaPorId(empresaId);
                        if (empresa != null) {
                            System.out.println("Pagamento efetuado para a empresa: " + empresa.getNomeFantasia());
                            usuarioService.atualizaStatus(empresa, StatusUsuario.AGUARDANDO_FORMULARIO);
                            empresaNome = empresa.getNomeFantasia();
                            status = "sucesso";
                        } else {
                            System.out.println("Empresa não encontrada com o ID: " + empresaId);
                        }
                    } else {
                        System.out.println("Metadata 'empresa_id' não encontrado.");
                    }

                    break;
                case "payment_intent.payment_failed":
                    Session session_failed = (Session) event.getData().getObject();

                    Map<String, String> metadata_fail = session_failed.getMetadata();

                    String empresaIdStringFail = metadata_fail.get("empresa_id");

                    if (empresaIdStringFail != null) {
                        UUID empresaId = UUID.fromString(empresaIdStringFail);
                        Empresa empresa = empresaService.buscaPorId(empresaId);
                        if (empresa != null) {
                            System.out.println("Pagamento falhou para a empresa: " + empresa.getNomeFantasia());
                            empresaNome = empresa.getNomeFantasia();
                            status = "falhou";
                        } else {
                            System.out.println("Empresa não encontrada com o ID: " + empresaId);
                        }
                    } else {
                        System.out.println("Metadata 'empresa_id' não encontrado.");
                    }
                    break;
                case "invoice.payment_failed":
                    Session session_failed_invoice = (Session) event.getData().getObject();

                    Map<String, String> metadata_fail_invoice = session_failed_invoice.getMetadata();

                    String empresaIdStringFailInvoice = metadata_fail_invoice.get("empresa_id");

                    if (empresaIdStringFailInvoice != null) {
                        UUID empresaId = UUID.fromString(empresaIdStringFailInvoice);
                        Empresa empresa = empresaService.buscaPorId(empresaId);
                        if (empresa != null) {
                            System.out.println("Pagamento falhou para a empresa: " + empresa.getNomeFantasia());
                            empresaNome = empresa.getNomeFantasia();
                            status = "falhou";
                        } else {
                            System.out.println("Empresa não encontrada com o ID: " + empresaId);
                        }
                    } else {
                        System.out.println("Metadata 'empresa_id' não encontrado.");
                    }
                    break;
                default:
                    System.out.println("Evento não tratado: " + event.getType());
                    break;
            }
            messagingTemplate.convertAndSend("/topic/status-payment", PagamentoStatusDto.builder().status(status).empresaNome(empresaNome).build());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
