package com.vulpix.api.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_usuario", columnDefinition = "varchar(36)")
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
    private boolean status;
    @Column(name = "telefone")
    private String telefone;
    @Column(name = "created_at")
    private LocalDateTime created_at;
    @Column(name = "updated_at")
    private LocalDateTime updated_at;

    public boolean isAtivo() {
        if (status) return true;
        return false;
    }
}
