package com.vulpix.api.service;

import com.stripe.Stripe;
import com.stripe.model.Event;
import com.stripe.model.PaymentLink;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentLinkCreateParams;
import com.vulpix.api.entity.Empresa;
import com.vulpix.api.exception.exceptions.ErroInternoException;
import com.vulpix.api.service.usuario.UsuarioService;
import com.vulpix.api.utils.enums.StatusUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class PagamentoService {
    @Autowired
    UsuarioService usuarioService;

    @Autowired
    EmpresaService empresaService;

    @Value("${stripe.chave-secreta}")
    private String stripeApiKey;

    public String criarPaymentLink(Empresa empresa) {
        Stripe.apiKey = stripeApiKey;

        PaymentLinkCreateParams params = PaymentLinkCreateParams.builder()
                .addLineItem(
                        PaymentLinkCreateParams.LineItem.builder()
                                .setPrice("price_1QTxq9Cv4ZKfs9w7yrImNc8J")
                                .setQuantity(1L)
                                .build())
                .putMetadata("empresa_id", empresa.getId().toString())
                .build();

        PaymentLink paymentLink;

        try {
            paymentLink = PaymentLink.create(params);
        } catch (Exception ex) {
            throw new ErroInternoException("Houve uma falha ao criar o link de pagamento.");
        }

        return paymentLink.getUrl();
    }

    public void criarWebhook(String payload, String sigHeader, String endpointSecret) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);

            switch (event.getType()) {
                case "checkout.session.completed", "payment_intent.succeeded", "invoice.payment_succeeded":
                    System.out.println("Evento: " + event.getType());
                    processarEvento(event, StatusUsuario.AGUARDANDO_FORMULARIO);
                    break;
                case "payment_intent.payment_failed", "invoice.payment_failed", "charge.failed":
                    System.out.println("Evento: " + event.getType());
                    processarEvento(event, null);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            throw new ErroInternoException("Houve uma falha ao criar o webhook.");
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