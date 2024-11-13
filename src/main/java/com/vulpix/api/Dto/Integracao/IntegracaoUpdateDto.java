package com.vulpix.api.Dto.Integracao;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IntegracaoUpdateDto {
    @NotBlank
    private String accessToken;
    @NotBlank
    private String clientId;
    @NotBlank
    private String clientSecret;
    @NotBlank
    private String igUserId;
}
