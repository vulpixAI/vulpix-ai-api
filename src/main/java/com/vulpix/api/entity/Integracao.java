package com.vulpix.api.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.vulpix.api.Enum.TipoIntegracao;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Integracao {
    @Id
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_integracao", columnDefinition = "varchar(36)")
    private UUID id;
    @Enumerated(EnumType.STRING)
    private TipoIntegracao tipo;
    @Column(name="ig_user_id")
    private String igUserId;
    @Column(name = "client_id")
    private String clientId;
    @Column(name = "client_secret")
    private String clientSecret;
    @Column(name = "access_token")
    private String accessToken;
    @Column(name = "access_token_expire_date")
    private LocalDateTime accessTokenExpireDate;
    @Column(name = "status")
    private Boolean status;

    @Column(name = "created_at")
    private LocalDateTime created_at;
    @Column(name = "updated_at")
    private LocalDateTime updated_at;
    @ManyToOne
    @JoinColumn(name = "fk_empresa", nullable = false)
    @JsonBackReference
    private Empresa empresa;
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public TipoIntegracao getTipo() {
        return tipo;
    }

    public void setTipo(TipoIntegracao tipo) {
        this.tipo = tipo;
    }

    public String getClient_id() {
        return clientId;
    }

    public void setClient_id(String client_id) {
        this.clientId = client_id;
    }

    public String getClient_secret() {
        return clientSecret;
    }

    public void setClient_secret(String client_secret) {
        this.clientSecret = client_secret;
    }

    public String getAccess_token() {
        return accessToken;
    }

    public void setAccess_token(String access_token) {
        this.accessToken = access_token;
    }

    public LocalDateTime getAccessTokenExpireDate() {
        return accessTokenExpireDate;
    }

    public void setAccessTokenExpireDate(LocalDateTime accessTokenExpireDate) {
        this.accessTokenExpireDate = accessTokenExpireDate;
    }

    public String getIgUserId() {
        return igUserId;
    }

    public void setIgUserId(String igUserId) {
        this.igUserId = igUserId;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public LocalDateTime getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }
}
