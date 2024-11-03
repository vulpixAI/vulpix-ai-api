package com.vulpix.api.Service.Integracoes.AgentAi;

import com.vulpix.api.Entity.Empresa;
import com.vulpix.api.Service.EmpresaService;
import com.vulpix.api.dto.Agent.PublicacaoGeradaResponse;
import com.vulpix.api.dto.Agent.PublicacaoGeradaRetorno;
import com.vulpix.api.dto.Empresa.FormularioRequisicaoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Service
public class CriativosService {

    private String URL = "http://192.168.0.7:5000/generate-content";
    @Autowired
    private RestTemplate restTemplate;

    public PublicacaoGeradaRetorno buscaCriativos(String prompt, String userRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("prompt", prompt);
        requestBody.put("user_request", userRequest);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        System.out.println("Requisição: " + requestBody);

        ResponseEntity<PublicacaoGeradaResponse> responseEntity = restTemplate.exchange(
                URL,
                HttpMethod.POST,
                requestEntity,
                PublicacaoGeradaResponse.class
        );

        PublicacaoGeradaResponse response = responseEntity.getBody();

        PublicacaoGeradaRetorno retorno = PublicacaoGeradaRetorno.builder()
                .legenda(response != null ? response.getCaption() : null)
                .build();

        if (response != null && response.getImage_urls() != null) {
            List<String> imageUrls = response.getImage_urls();

            if (imageUrls.size() > 0) retorno.setImagem1(imageUrls.get(0));
            if (imageUrls.size() > 1) retorno.setImagem2(imageUrls.get(1));
            if (imageUrls.size() > 2) retorno.setImagem3(imageUrls.get(2));
            if (imageUrls.size() > 3) retorno.setImagem4(imageUrls.get(3));
        }

        return retorno;
    }
}