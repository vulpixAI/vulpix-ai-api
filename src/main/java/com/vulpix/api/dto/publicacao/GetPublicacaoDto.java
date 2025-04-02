package com.vulpix.api.dto.publicacao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GetPublicacaoDto {
    @JsonProperty(value = "id")
    private String id;
    @JsonProperty(value = "caption")
    private String legenda;
    @JsonProperty(value = "media_type")
    private String tipoMidia;
    @JsonProperty(value = "media_url")
    private String urlMidia;
    @JsonProperty(value = "timestamp")
    private OffsetDateTime dataPublicacao;
    @JsonProperty(value = "like_count")
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

