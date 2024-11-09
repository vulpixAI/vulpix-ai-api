package com.vulpix.api.Dto.Criativo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CriativoRequisicaoDto {
    private String imageUrl;
    private String prompt;
}
