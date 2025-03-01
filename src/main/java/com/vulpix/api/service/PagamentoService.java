package com.vulpix.api.service;

import com.stripe.Stripe;
import com.stripe.model.PaymentLink;
import com.stripe.param.PaymentLinkCreateParams;
import com.vulpix.api.entity.Empresa;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PagamentoService {
    @Value("${stripe.chave-secreta}")
    private String stripeApiKey;
    public String criarPaymentLink(Empresa empresa) throws Exception {
        Stripe.apiKey = stripeApiKey;

        PaymentLinkCreateParams params = PaymentLinkCreateParams.builder()
                .addLineItem(
                        PaymentLinkCreateParams.LineItem.builder()
                                .setPrice("price_1QTxq9Cv4ZKfs9w7yrImNc8J")
                                .setQuantity(1L)
                                .build())
                .putMetadata("empresa_id", empresa.getId().toString())
                .build();

        PaymentLink paymentLink = PaymentLink.create(params);

        return paymentLink.getUrl();
    }
}
