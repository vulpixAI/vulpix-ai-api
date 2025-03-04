package com.vulpix.api.dto.Dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashKpiDto {
    private BigDecimal taxaCompartilhamento;
    private BigDecimal taxaSaves;
    private Integer visualizacoesTotais;
    private Integer alcanceUltimoPost;
}