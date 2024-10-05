package com.vulpix.api.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.vulpix.api.Enum.TipoIntegracao;
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
public class Integracao {
    @Id
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_integracao", columnDefinition = "varchar(36)")
    private UUID id;
    @Enumerated(EnumType.STRING)
    private TipoIntegracao tipo;
    @Column(name="ig_user_id")
    private String igUserId;
    @Column(name = "client_id")
    private String clientId;
    @Column(name = "client_secret")
    private String clientSecret;
    @Column(name = "access_token")
    private String accessToken;
    @Column(name = "access_token_expire_date")
    private LocalDateTime accessTokenExpireDate;
    @Column(name = "status")
    private Boolean status;

    @Column(name = "created_at")
    private LocalDateTime created_at;
    @Column(name = "updated_at")
    private LocalDateTime updated_at;
    @ManyToOne
    @JoinColumn(name = "fk_empresa", nullable = false)
    @JsonBackReference
    private Empresa empresa;
}
