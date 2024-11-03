package com.vulpix.api.Dto.Agent;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PublicacaoGeradaRequest {
    private String prompt;
    private String userRequest;
}
