package com.vulpix.api.dto.pagamento;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PagamentoStatusDto {
    private String status;
    private String empresaNome;
}
