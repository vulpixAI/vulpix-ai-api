package com.vulpix.api.Dto.Integracao;

import com.vulpix.api.Utils.Enum.TipoIntegracao;
import jakarta.validation.constraints.NotNull;
import lombok.*;

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
