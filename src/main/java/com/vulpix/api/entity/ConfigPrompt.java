package com.vulpix.api.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

public class ConfigPrompt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_config_prompt")
    private Integer id;
    @Column(name = "chave")
    private String chave;
    @Column(name = "valor")
    private String valor;
    @ManyToOne
    @JoinColumn(name = "fk_empresa", nullable = false)
    private Empresa empresa;
    @Column(name = "created_at")
    private LocalDateTime created_at;
    @Column(name = "updated_at")
    private LocalDateTime updated_at;
}
