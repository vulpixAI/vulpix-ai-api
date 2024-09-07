package com.vulpix.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PostPublicacaoDto {
    @JsonProperty(value = "image_url")
    private String imageUrl;
    @JsonProperty(value = "caption")
    private String caption;
    @JsonProperty(value = "fk_empresa")
    private Integer fkEmpresa;

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

    public Integer getFkEmpresa() {
        return fkEmpresa;
    }

    public void setFkEmpresa(Integer fkEmpresa) {
        this.fkEmpresa = fkEmpresa;
    }
}
