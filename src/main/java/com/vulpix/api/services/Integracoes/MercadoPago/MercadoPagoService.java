package com.vulpix.api.services.Integracoes.MercadoPago;

import com.mercadopago.MercadoPago;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.Preference;
import com.mercadopago.resources.datastructures.preference.Item;
import com.mercadopago.resources.datastructures.preference.Payer;
import com.vulpix.api.entity.Plano;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MercadoPagoService {

    @Value("${mercadopago.access-token}")
    private String accessToken;

    public MercadoPagoService() {
        try {
            MercadoPago.SDK.setAccessToken(null);
        } catch (MPException e) {
            e.printStackTrace();
        }
    }

    public String criarPreferenciaDePagamento(Plano plano, String emailCliente) throws MPException {
        Preference preferencia = new Preference();

        Item item = new Item()
                .setTitle(plano.getNome())
                .setQuantity(1)
                .setUnitPrice(plano.getPreco().floatValue());


        preferencia.appendItem(item);


        Payer pagador = new Payer();
        pagador.setEmail(emailCliente);
        preferencia.setPayer(pagador);


        return preferencia.save().getSandboxInitPoint(); // Para ambiente de sandbox
    }
}
