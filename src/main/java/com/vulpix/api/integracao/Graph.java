package com.vulpix.api.integracao;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public class Graph implements Integracao{

    private String client_id;
    private String client_secret;
    private String access_token;
    private LocalDateTime access_token_expire_date;
    private Boolean status;

    @Override
    public String obterAccessToken() {
        return null;
    }

    @Override
    public String renovarAccessToken() {
        return null;
    }

    @Override
    public Boolean verificarAccessToken() {
        if (access_token_expire_date != null && access_token_expire_date.isAfter(LocalDateTime.now())){
            renovarAccessToken();
        }
        return true;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getClient_secret() {
        return client_secret;
    }

    public void setClient_secret(String client_secret) {
        this.client_secret = client_secret;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public LocalDateTime getAccess_token_expire_date() {
        return access_token_expire_date;
    }

    public void setAccess_token_expire_date(LocalDateTime access_token_expire_date) {
        this.access_token_expire_date = access_token_expire_date;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
