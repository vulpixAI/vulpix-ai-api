package com.vulpix.api.Entity;

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
    @Column(name = "id_post_insight")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publicacao_id", nullable = false)
    private Publicacao publicacao;

    @Column(name = "post_id", nullable = false)
    private String postId;

    @Column(name = "likes", nullable = false)
    private Integer likes;

    @Column(name = "views", nullable = false)
    private Integer views;

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
}
