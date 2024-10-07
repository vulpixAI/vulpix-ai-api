package com.vulpix.api.Dto.Integracao.Resquest;

import com.vulpix.api.Enum.TipoIntegracao;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class IntegracaoDto {
    @NotBlank
    private TipoIntegracao tipo;
    @NotBlank
    private UUID idEmpresa;
}
