package com.vulpix.api.Controller;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import com.stripe.model.Event;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pagamentos")
public class StripeWebhookController {
    private static final String STRIPE_SECRET = System.getenv("STRIPE_SECRET");

    @PostMapping("/webhook-stripe")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload,
                                                      @RequestHeader("Stripe-Signature") String signature) {
        try {
            Event event = Webhook.constructEvent(
                    payload, signature, STRIPE_SECRET
            );

            if ("payment_intent.succeeded".equals(event.getType())) {
                PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer()
                        .getObject()
                        .orElseThrow();
                String userId = paymentIntent.getMetadata().get("userId");

                System.out.println("Pagamento bem-sucedido para o usuário: " + userId);
            } else if ("payment_intent.payment_failed".equals(event.getType())) {
                System.out.println("Falha no pagamento");
            }

            return ResponseEntity.status(200).body("Webhook recebido com sucesso");
        } catch (SignatureVerificationException e) {
            System.out.println("Falha na verificação da assinatura: " + e.getMessage());
            return ResponseEntity.status(400).body("Falha na assinatura");
        } catch (Exception e) {
            System.out.println("Erro ao processar o webhook: " + e.getMessage());
            return ResponseEntity.status(500).body("Erro interno");
        }
    }
}
