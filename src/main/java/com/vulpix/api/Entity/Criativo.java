package com.vulpix.api.Entity;

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
public class Criativo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_criativo")
    private UUID id;

    @Column(name = "image_url", length = 2048)
    private String imageUrl;

    @Column(name = "prompt", length = 1500)
    private String prompt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_empresa", referencedColumnName = "id_empresa", nullable = false)
    private Empresa empresa;
}
