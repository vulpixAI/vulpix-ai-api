package com.vulpix.api.Dto.Agent;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PublicacaoGeradaRequest {
    @NotBlank
    private String prompt;
    @NotBlank
    private String userRequest;
}
