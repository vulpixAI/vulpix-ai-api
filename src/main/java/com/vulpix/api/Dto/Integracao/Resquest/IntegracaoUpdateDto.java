package com.vulpix.api.Dto.Integracao.Resquest;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IntegracaoUpdateDto {
    private String accessToken;
    private String clientId;
    private String clientSecret;
    private String igUserId;
}
