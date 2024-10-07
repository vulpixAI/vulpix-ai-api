package com.vulpix.api.controller;

import com.mercadopago.exceptions.MPException;
import com.vulpix.api.dto.Pagamento.Req.PagamentoRequestDto;
import com.vulpix.api.dto.Pagamento.Res.PagamentoResponseDto;
import com.vulpix.api.services.Integracoes.MercadoPago.PagamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {

    @Autowired
    private PagamentoService pagamentoService;

    @PostMapping("/mercadopago")
    public PagamentoResponseDto iniciarPagamento(@RequestBody PagamentoRequestDto pagamentoRequestDTO) {
        try {
            return pagamentoService.processarPagamento(pagamentoRequestDTO);
        } catch (MPException e) {
            throw new RuntimeException("Erro ao processar pagamento: " + e.getMessage());
        }
    }
}