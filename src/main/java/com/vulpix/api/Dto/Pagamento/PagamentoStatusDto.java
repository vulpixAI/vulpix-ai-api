package com.vulpix.api.Dto.Pagamento;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PagamentoStatusDto {
    private String status;
    private String empresaNome;
}
