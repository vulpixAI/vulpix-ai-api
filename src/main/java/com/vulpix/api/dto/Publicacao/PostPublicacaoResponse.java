package com.vulpix.api.dto.Publicacao;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class PostPublicacaoResponse {
    @JsonProperty(value = "id")
    private UUID id;

    @JsonProperty(value = "fk_empresa")
    private UUID fkEmpresa;

    @JsonProperty(value = "legenda")
    private String legenda;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getFkEmpresa() {
        return fkEmpresa;
    }

    public void setFkEmpresa(UUID fkEmpresa) {
        this.fkEmpresa = fkEmpresa;
    }

    public String getLegenda() {
        return legenda;
    }

    public void setLegenda(String legenda) {
        this.legenda = legenda;
    }
}
