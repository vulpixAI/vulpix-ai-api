package com.vulpix.api.Dto.Publicacao;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.FutureOrPresent;
import org.hibernate.validator.constraints.URL;

import java.time.OffsetDateTime;
import java.util.UUID;

public class PostPublicacaoDto {
    @JsonProperty(value = "image_url")
    @URL
    private String imageUrl;
    @JsonProperty(value = "caption")
    private String caption;
    @JsonProperty(value = "data_agendamento")
    @FutureOrPresent
    private OffsetDateTime agendamento;

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

}
