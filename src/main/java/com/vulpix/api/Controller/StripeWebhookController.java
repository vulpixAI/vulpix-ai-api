package com.vulpix.api.Controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import com.stripe.model.Event;
import com.vulpix.api.Entity.Empresa;
import com.vulpix.api.Service.Usuario.Autenticacao.UsuarioAutenticadoUtil;
import com.vulpix.api.Service.Usuario.UsuarioService;
import com.vulpix.api.Utils.Enum.StatusUsuario;
import com.vulpix.api.Utils.Helpers.EmpresaHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pagamentos")
public class StripeWebhookController {
    private static final String STRIPE_SECRET = System.getenv("STRIPE_SECRET");
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private EmpresaHelper empresaHelper;

    @Autowired
    private UsuarioAutenticadoUtil usuarioAutenticadoUtil;

// Descomente o código abaixo para poder rodar em produção:

//    @PostMapping()
//    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload,
//                                                      @RequestHeader("Stripe-Signature") String signature) {
//
//        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
//        String emailUsuario = userDetails.getUsername();
//        Empresa empresa = empresaHelper.buscarEmpresaPeloUsuario(emailUsuario);
//
//        try {
//            Event event = Webhook.constructEvent(
//                    payload, signature, STRIPE_SECRET
//            );
//
//            if ("payment_intent.succeeded".equals(event.getType())) {
//                PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer()
//                        .getObject()
//                        .orElseThrow();
//                String userId = paymentIntent.getMetadata().get("userId");
//
//                System.out.println("Pagamento bem-sucedido para o usuário: " + userId);
//            } else if ("payment_intent.payment_failed".equals(event.getType())) {
//                System.out.println("Falha no pagamento");
//            }
//            UsuarioService usuarioService = new UsuarioService();
//            usuarioService.atualizaStatus(empresa, StatusUsuario.AGUARDANDO_FORMULARIO);
//            return ResponseEntity.status(200).body("Webhook recebido com sucesso");
//        } catch (SignatureVerificationException e) {
//            System.out.println("Falha na verificação da assinatura: " + e.getMessage());
//            return ResponseEntity.status(400).body("Falha na assinatura");
//        } catch (Exception e) {
//            System.out.println("Erro ao processar o webhook: " + e.getMessage());
//            return ResponseEntity.status(500).body("Erro interno");
//        }
//    }


// Descomente o código abaixo para poder rodar em desenvolvimento:

    @PostMapping
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(payload);
            String eventType = rootNode.get("type").asText();

            if ("payment_intent.succeeded".equals(eventType)) {
                JsonNode metadata = rootNode.path("data").path("object").path("metadata");
                String userId = metadata.get("userId").asText();
                System.out.println("Pagamento bem-sucedido para o usuário: " + userId);
            }

            return ResponseEntity.status(200).body("Webhook recebido com sucesso");
        } catch (Exception e) {
            System.out.println("Erro ao processar o webhook: " + e.getMessage());
            return ResponseEntity.status(500).body("Erro interno");
        }
    }
}
