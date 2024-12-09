package com.vulpix.api.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.vulpix.api.Utils.Enum.TipoIntegracao;
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
public class Integracao {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_integracao")
    private UUID id;
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo")
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
