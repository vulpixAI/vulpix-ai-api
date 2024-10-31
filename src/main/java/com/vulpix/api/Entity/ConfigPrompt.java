package com.vulpix.api.Entity;

import com.vulpix.api.dto.Empresa.FormularioRequisicaoDto;
import com.vulpix.api.dto.Empresa.JsonConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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

    @Convert(converter = JsonConverter.class)
    @Column(columnDefinition = "json")
    private FormularioRequisicaoDto form;

    @OneToOne
    @JoinColumn(name = "fk_empresa", nullable = false)
    private Empresa empresa;
}
