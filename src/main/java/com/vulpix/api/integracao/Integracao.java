package com.vulpix.api.integracao;

public interface Integracao {
    public abstract String obterAccessToken();
    public abstract String renovarAccessToken();
    public abstract Boolean verificarAccessToken(); // Retorna se o token expirou

}
