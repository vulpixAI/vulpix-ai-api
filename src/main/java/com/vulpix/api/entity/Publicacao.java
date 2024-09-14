package com.vulpix.api.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
public class Publicacao {
    @Id
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_publicacao", columnDefinition = "varchar(36)")
    private UUID id;
    @Column(name = "legenda")
    @JsonProperty("caption")
    private String legenda;
    @Column(name = "tipo")
    @JsonProperty("media_type")
    private String tipoMidia;
    @Column(name = "image_url", columnDefinition = "varchar(2048)")
    @JsonProperty("media_url")
    private String urlMidia;
    @Column(name = "data_agendamento")
    @JsonProperty("timestamp")
    private OffsetDateTime dataPublicacao;
    @Column(name = "total_like")
    @JsonProperty("like_count")
    private Integer likeCount;
    @Column(name = "plataforma")
    private String plataforma;
    @Column(name = "created_at")
    private LocalDateTime created_at;
    @Column(name = "id_returned")
    private String idReturned;

    @ManyToOne
    @JoinColumn(name = "fk_empresa", nullable = false)
    private Empresa empresa;

    public String getPlataforma() {
        return plataforma;
    }

    public void setPlataforma(String plataforma) {
        this.plataforma = plataforma;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
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

    public String getIdReturned() {
        return idReturned;
    }

    public void setIdReturned(String idReturned) {
        this.idReturned = idReturned;
    }
}
