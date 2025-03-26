package com.vulpix.api.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.vulpix.api.utils.enums.StatusUsuario;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_usuario")
    private UUID id;
    @Column(name = "nome")
    private String nome;
    @Column(name = "sobrenome")
    private String sobrenome;
    @Column(name = "email")
    private String email;
    @Column(name = "senha")
    private String senha;
    @Column(name = "status")
    private StatusUsuario status;
    @Column(name = "telefone")
    private String telefone;
    @Column(name = "secret_key")
    private String secretKey;
    @Column(name = "created_at")
    private LocalDateTime created_at;
    @Column(name = "updated_at")
    private LocalDateTime updated_at;

    @OneToOne(mappedBy = "usuario")
    @JsonBackReference
    private Empresa empresa;
}
