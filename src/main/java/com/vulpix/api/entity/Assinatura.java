package com.vulpix.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Assinatura {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_assinatura")
    private UUID id;

    @Column(name = "data_inicio")
    private LocalDate dataInicio;

    @Column(name = "data_expiracao")
    private LocalDate dataExpiracao;

    @Column(name = "preco")
    private Double preco;

    @Column(name = "status")
    private String status;

    @OneToOne
    @JoinColumn(name = "fk_usuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "fk_plano")
    private Plano plano;
}
