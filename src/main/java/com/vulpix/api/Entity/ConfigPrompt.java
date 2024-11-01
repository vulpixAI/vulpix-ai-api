package com.vulpix.api.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConfigPrompt {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_config_prompt")
    private UUID id;

    @Column(name = "form")
    private String form;

    @Column(name = "prompt")
    private String prompt;

    @OneToOne
    @JoinColumn(name = "fk_empresa", nullable = false)
    private Empresa empresa;
}
