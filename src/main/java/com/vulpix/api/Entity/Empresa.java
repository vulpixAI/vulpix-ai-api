package com.vulpix.api.Entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Empresa {
    @Id
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_empresa", columnDefinition = "varchar(36)")
    private UUID id;
    @Column(name = "razao_social")
    private String razaoSocial;

    @Column(name = "nome_fantasia")
    private String nomeFantasia;
    @Column(name = "cnpj")
    private String cnpj;
    @Column(name = "cep")
    private String cep;
    @Column(name = "logradouro")
    private String logradouro;
    @Column(name = "numero")
    private String numero;
    @Column(name = "bairro")
    private String bairro;
    @Column(name = "complemento")
    private String complemento;
    @Column(name = "cidade")
    private String cidade;
    @Column(name = "estado")
    private String estado;
    @Column(name = "created_at")
    private LocalDateTime created_at;
    @Column(name = "updated_at")
    private LocalDateTime updated_at;

    @OneToOne
    @JoinColumn(name = "responsavel", nullable = false)
    private Usuario usuario;
    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Integracao> integracoes;

}
