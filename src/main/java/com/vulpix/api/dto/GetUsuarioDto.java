package com.vulpix.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.UUID;

public class GetUsuarioDto {
    @JsonProperty(value = "id_usuario")
    private UUID id;
    @JsonProperty(value = "nome")
    private String nome;
    @JsonProperty(value = "sobrenome")
    private String sobrenome;
    @JsonProperty(value = "email")
    private String email;
    @JsonProperty(value = "status")
    private boolean status;
    @JsonProperty(value = "telefone")
    private String telefone;
    @JsonProperty(value = "created_at")
    private LocalDateTime created_at;
    @JsonProperty(value = "updated_at")
    private LocalDateTime updated_at;

    public GetUsuarioDto(UUID id, String nome, String sobrenome, String email, boolean status, String telefone, LocalDateTime created_at, LocalDateTime updated_at) {
        this.id = id;
        this.nome = nome;
        this.sobrenome = sobrenome;
        this.email = email;
        this.status = status;
        this.telefone = telefone;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSobrenome() {
        return sobrenome;
    }

    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
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
