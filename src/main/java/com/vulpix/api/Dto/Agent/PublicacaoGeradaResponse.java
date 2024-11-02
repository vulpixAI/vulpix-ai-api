package com.vulpix.api.dto.Agent;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data
@Builder
public class PublicacaoGeradaResponse {

    private String caption;

    private List<String> imageUrls;
}
