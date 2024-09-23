package com.vulpix.api.config;

import com.mercadopago.MercadoPago;
import org.springframework.context.annotation.Bean;


public class MercadoPagoConfig {
    private String publicKey;
    private String accessToken;


    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Bean
    void setMercadoPagoAccessToken() throws Exception {
        MercadoPago.SDK.setAccessToken(this.accessToken);
    }

}
