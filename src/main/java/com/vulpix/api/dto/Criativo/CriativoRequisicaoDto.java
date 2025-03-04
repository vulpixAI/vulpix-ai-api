package com.vulpix.api.dto.Criativo;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
@Builder
public class CriativoRequisicaoDto {
    @URL
    private String imageUrl;
    @NotBlank
    private String prompt;
}
