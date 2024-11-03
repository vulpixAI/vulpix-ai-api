package com.vulpix.api.Service.Integracoes.AgentAi;

import com.vulpix.api.Dto.Agent.PublicacaoGeradaResponse;
import com.vulpix.api.Dto.Agent.PublicacaoGeradaRetorno;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CriativosService {


    @Autowired
    private RestTemplate restTemplate;

    public PublicacaoGeradaRetorno buscaCriativos(String prompt, String userRequest) {
        String URL = "http://192.168.0.7:5000/generate-content";
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

    public String buscaLegenda(String userRequest) {
        String URL = "http://192.168.0.7:5000/generate-caption";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("user_request", userRequest);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        System.out.println("Requisição: " + requestBody);

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                URL,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        String response = responseEntity.getBody();
        return response;
    }
}
