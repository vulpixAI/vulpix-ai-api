package com.vulpix.api.dto.criativo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CriativoUnitDto {
    private UUID id;
    private String image_url;
}
