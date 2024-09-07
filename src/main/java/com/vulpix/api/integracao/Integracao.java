package com.vulpix.api.integracao;

public interface Integracao {
    public abstract String obterAccessToken();
    public abstract String renovarAccessToken();
    public abstract Boolean verificarAccessToken(); // Retorna se o token expirou
    public abstract String criarContainer(); // Retorna id do container
    public abstract String criarPublicacao(); // Retorna id do post

}
