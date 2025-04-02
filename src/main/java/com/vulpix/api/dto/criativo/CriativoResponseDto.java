package com.vulpix.api.dto.criativo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CriativoResponseDto {
    private String prompt;
    private List<CriativoUnitDto> images;
}
