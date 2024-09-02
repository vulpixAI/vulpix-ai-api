package com.vulpix.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public class PublicacaoDto {
    private String id;
    private String legenda;
    private String tipoMidia;
    private String urlMidia;
    private OffsetDateTime dataPublicacao;

    private Integer likeCount;

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLegenda() {
        return legenda;
    }

    public void setLegenda(String legenda) {
        this.legenda = legenda;
    }

    public String getTipoMidia() {
        return tipoMidia;
    }

    public void setTipoMidia(String tipoMidia) {
        this.tipoMidia = tipoMidia;
    }

    public String getUrlMidia() {
        return urlMidia;
    }

    public void setUrlMidia(String urlMidia) {
        this.urlMidia = urlMidia;
    }

    public OffsetDateTime getDataPublicacao() {
        return dataPublicacao;
    }

    public void setDataPublicacao(OffsetDateTime dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
    }
}
