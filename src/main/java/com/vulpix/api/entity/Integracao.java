package com.vulpix.api.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Integracao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_integracao")
    private Integer id;
    @Column(name = "client_id")
    private String client_id;
    @Column(name = "client_secret")
    private String client_secret;
    @Column(name = "access_token")
    private String access_token;
    @Column(name = "access_token_expire_date")
    private LocalDateTime access_token_expire_date;
    @Column(name = "status")
    private Boolean status;
    @OneToMany
    @JoinColumn(name = "fk_empresa", nullable = false)
    private Empresa empresa;

    @Column(name = "created_at")
    private LocalDateTime created_at;
    @Column(name = "updated_at")
    private LocalDateTime updated_at;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getClient_secret() {
        return client_secret;
    }

    public void setClient_secret(String client_secret) {
        this.client_secret = client_secret;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public LocalDateTime getAccess_token_expire_date() {
        return access_token_expire_date;
    }

    public void setAccess_token_expire_date(LocalDateTime access_token_expire_date) {
        this.access_token_expire_date = access_token_expire_date;
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
