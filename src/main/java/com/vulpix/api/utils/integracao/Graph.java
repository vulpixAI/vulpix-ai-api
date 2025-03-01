package com.vulpix.api.utils.integracao;

import java.time.LocalDateTime;

public class Graph implements Integracao{

    private String clientId;
    private String clientSecret;
    private String accessToken;
    private LocalDateTime accessTokenExpireDate;
    private Boolean status;

    private String igUserId;
    public static final String BASE_URL = "https://graph.facebook.com/v17.0/";
    public static final String FIELDS = "id,caption,media_type,media_url,timestamp,like_count";

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
        if (accessTokenExpireDate != null && accessTokenExpireDate.isAfter(LocalDateTime.now())){
            renovarAccessToken();
        }
        return true;
    }

    @Override
    public String criarContainer() {
        return null;
    }

    @Override
    public String criarPublicacao() {
        return null;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public LocalDateTime getAccessTokenExpireDate() {
        return accessTokenExpireDate;
    }

    public void setAccessTokenExpireDate(LocalDateTime accessTokenExpireDate) {
        this.accessTokenExpireDate = accessTokenExpireDate;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getIgUserId() {
        return igUserId;
    }

    public void setIgUserId(String igUserId) {
        this.igUserId = igUserId;
    }
}
