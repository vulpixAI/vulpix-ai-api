package com.vulpix.api.services.Integracoes.MercadoPago;

import com.mercadopago.MercadoPago;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.Preference;
import com.mercadopago.resources.datastructures.preference.Item;
import com.mercadopago.resources.datastructures.preference.Payer;
import com.vulpix.api.dto.Pagamento.Req.PagamentoRequestDto;
import com.vulpix.api.dto.Pagamento.Res.PagamentoResponseDto;
import com.vulpix.api.entity.Plano;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PagamentoService {

    @Value("${mercadopago.access-token}")
    private String accessToken;

    public MercadoPagoService() {
        try {
            MercadoPago.SDK.setAccessToken(accessToken);
        } catch (MPException e) {
            e.printStackTrace();
        }
    }

    public String criarPreferenciaDePagamento(Plano plano, String emailCliente) throws MPException {
        // Cria a preferência de pagamento
        Preference preferencia = new Preference();

        // Cria o item do plano
        Item item = new Item()
                .setTitle(plano.getNome())
                .setQuantity(1)
                .setUnitPrice(plano.getPreco().floatValue());

        // Adiciona o item à preferência
        preferencia.appendItem(item);

        // Define o cliente pagador
        Payer pagador = new Payer();
        pagador.setEmail(emailCliente);
        preferencia.setPayer(pagador);

        // Salva e retorna a URL da preferência de pagamento
        return preferencia.save().getSandboxInitPoint(); // Para ambiente de sandbox, use `getInitPoint()` em produção
    }

    public PagamentoResponseDto processarPagamento(PagamentoRequestDto pagamentoRequestDTO) throws MPException {
        // 1. Buscar o plano no banco de dados pelo ID
        Plano plano = planoRepository.findById(pagamentoRequestDTO.getPlanoId())
                .orElseThrow(() -> new RuntimeException("Plano não encontrado"));

        // 2. Criar a preferência de pagamento no Mercado Pago
        String urlPagamento = mercadoPagoService.criarPreferenciaDePagamento(plano, pagamentoRequestDTO.getEmailCliente());

        // 3. Preparar a resposta com a URL do pagamento
        PagamentoResponseDto responseDTO = new PagamentoResponseDto();
        responseDTO.setUrlPagamento(urlPagamento);

        // 4. Retornar a resposta com a URL para o cliente
        return responseDTO;
    }
}