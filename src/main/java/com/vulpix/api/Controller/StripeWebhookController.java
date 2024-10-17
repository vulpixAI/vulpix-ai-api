package com.vulpix.api.Controller;



import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook/stripe")
public class StripeWebhookController {

    @Value("${STRIPE_WEBHOOK_SECRET}")
    private String stripeWebhookSecret;

    @PostMapping
    public String handleStripeWebhook(@RequestBody String payload,
                                      @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            // Verifica se o webhook é legítimo (não forjado)
            Event event = Webhook.constructEvent(payload, sigHeader, stripeWebhookSecret);

            // Tratar o evento de pagamento concluído
            if ("checkout.session.completed".equals(event.getType())) {
                Session session = (Session) event.getDataObjectDeserializer().getObject().orElseThrow();
                String customerEmail = session.getCustomerDetails().getEmail();

                // Lógica para quando o pagamento for bem-sucedido
                System.out.println("Pagamento concluído para o email: " + customerEmail);

                // Aqui, você pode marcar o pagamento como concluído no banco de dados,
                // liberar o acesso à dashboard, ou outras ações necessárias
            }

            return "Webhook processado com sucesso";

        } catch (Exception e) {
            e.printStackTrace();
            return "Erro no processamento do webhook";
        }
    }
}

