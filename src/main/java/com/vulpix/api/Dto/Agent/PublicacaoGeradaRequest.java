package com.vulpix.api.dto.Agent;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PublicacaoGeradaRequest {
    private String prompt;
    private String userRequest;
}
