package com.vulpix.api.services.Integracoes.MercadoPago;

import com.mercadopago.exceptions.MPException;
import com.vulpix.api.dto.Pagamento.Req.PagamentoRequestDto;
import com.vulpix.api.dto.Pagamento.Res.PagamentoResponseDto;
import com.vulpix.api.entity.Plano;
import com.vulpix.api.repository.PlanoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PagamentoService {

    @Autowired
    private PlanoRepository planoRepository;
    @Autowired
    private MercadoPagoService mercadoPagoService;

    public PagamentoResponseDto processarPagamento(PagamentoRequestDto pagamentoRequestDTO) throws MPException {
        Plano plano = planoRepository.findById(pagamentoRequestDTO.getPlanoId())
                .orElseThrow(() -> new RuntimeException("Plano não encontrado"));
        String urlPagamento = mercadoPagoService.criarPreferenciaDePagamento(plano, pagamentoRequestDTO.getEmailCliente());
        PagamentoResponseDto responseDTO = new PagamentoResponseDto();
        responseDTO.setUrlPagamento(urlPagamento);
        return responseDTO;
    }
}
