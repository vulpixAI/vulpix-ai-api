package com.vulpix.api.dto.Integracao.Resquest;

import com.vulpix.api.Enum.TipoIntegracao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class IntegracaoDto {
    @NotNull
    private TipoIntegracao tipo;
}
