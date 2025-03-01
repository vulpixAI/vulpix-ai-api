package com.vulpix.api.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostInsights {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_insight")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_publicacao", nullable = false)
    @JsonBackReference
    private Publicacao publicacao;

    @Column(name = "likes", nullable = false)
    private Integer likes;

    @Column(name = "comments", nullable = false)
    private Integer comments;

    @Column(name = "shares", nullable = false)
    private Integer shares;

    @Column(name = "saves", nullable = false)
    private Integer saves;

    @Column(name = "impressions", nullable = false)
    private Integer impressions;

    @Column(name = "profile_visits", nullable = false)
    private Integer profileVisits;

    @Column(name = "follows", nullable = false)
    private Integer follows;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @JsonProperty("fk_publicacao")
    public UUID getFkPublicacao() {
        return publicacao != null ? publicacao.getId() : null;
    }
}
