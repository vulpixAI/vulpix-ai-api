package com.vulpix.api.dto.criativo;

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
