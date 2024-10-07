package com.vulpix.api.dto.Pagamento.Req;

import lombok.Data;

@Data
public class PagamentoRequestDto {
    private Integer planoId;
    private String emailCliente;
    private String metodoPagamento;
}