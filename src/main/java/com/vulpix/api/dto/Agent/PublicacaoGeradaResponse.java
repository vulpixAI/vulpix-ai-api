package com.vulpix.api.dto.Agent;

import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data
@Builder
public class PublicacaoGeradaResponse {

    private String caption;

    private List<String> image_urls;
}
