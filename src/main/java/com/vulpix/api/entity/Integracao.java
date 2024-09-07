package com.vulpix.api.entity;

import com.vulpix.api.Enum.TipoIntegracao;
import jakarta.persistence.*;

import java.time.LocalDateTime;
@Entity
public class Integracao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_integracao")
    private Integer id;
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
    private Empresa empresa;
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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
