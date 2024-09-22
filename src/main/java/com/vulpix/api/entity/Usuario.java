package com.vulpix.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.cache.annotation.CacheConfig;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;


@Entity
public class Usuario {
    @Id
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_usuario", columnDefinition = "varchar(36)")
    private UUID id;
    @Column(name = "nome")
    @NotBlank
    @Size(min = 5, max = 150)
    private String nome;
    @Column(name = "sobrenome")
    @NotBlank
    @Size(min = 5, max = 150)
    private String sobrenome;
    @Column(name = "email")
    @NotBlank
    @Email
    private String email;
    @Column(name = "senha")
    @NotBlank
    @Size(min = 8)
    private String senha;
    @Column(name = "status")
    private boolean status;
    @Column(name = "telefone")
    @NotBlank
    @Pattern(regexp = "\\(\\d{2}\\) \\d{4,5}-\\d{4}", message = "Formato de telefone inválido. Use o formato (XX) XXXX-XXXX ou (XX) XXXXX-XXXX")
    private String telefone;
    @Column(name = "created_at")
    private LocalDateTime created_at;
    @Column(name = "updated_at")
    private LocalDateTime updated_at;

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

    public boolean isStatus() {
        return status;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }


    public boolean isAtivo() {
        return status;
    }

    public void setAtivo(boolean ativo) {
        this.status = ativo;
    }

}
