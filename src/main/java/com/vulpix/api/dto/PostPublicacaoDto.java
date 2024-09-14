package com.vulpix.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

public class PostPublicacaoDto {
    @JsonProperty(value = "image_url")
    private String imageUrl;
    @JsonProperty(value = "caption")
    private String caption;
    @JsonProperty(value = "data_agendamento")
    private OffsetDateTime agendamento;
    @JsonProperty(value = "fk_empresa")
    private UUID fkEmpresa;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public OffsetDateTime getAgendamento() {
        return agendamento;
    }

    public void setAgendamento(OffsetDateTime agendamento) {
        this.agendamento = agendamento;
    }

    public UUID getFkEmpresa() {
        return fkEmpresa;
    }

    public void setFkEmpresa(UUID fkEmpresa) {
        this.fkEmpresa = fkEmpresa;
    }
}
