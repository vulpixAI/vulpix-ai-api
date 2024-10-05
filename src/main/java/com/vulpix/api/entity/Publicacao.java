package com.vulpix.api.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Publicacao {
    @Id
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_publicacao", columnDefinition = "varchar(36)")
    private UUID id;
    @Column(name = "legenda")
    @JsonProperty("caption")
    private String legenda;
    @Column(name = "tipo")
    @JsonProperty("media_type")
    private String tipoMidia;
    @Column(name = "image_url", columnDefinition = "varchar(2048)")
    @JsonProperty("media_url")
    private String urlMidia;
    @Column(name = "data_agendamento")
    @JsonProperty("timestamp")
    private OffsetDateTime dataPublicacao;
    @Column(name = "total_like")
    @JsonProperty("like_count")
    private Integer likeCount;
    @Column(name = "plataforma")
    private String plataforma;
    @Column(name = "created_at")
    private LocalDateTime created_at;
    @Column(name = "id_returned")
    private String idReturned;

    @ManyToOne
    @JoinColumn(name = "fk_empresa", nullable = false)
    private Empresa empresa;
}
