package com.vulpix.api.dto.integracao;

import com.vulpix.api.utils.enums.TipoIntegracao;
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
