package com.vulpix.api.Service.Integracoes.AgentAi;

import com.vulpix.api.Entity.Empresa;
import com.vulpix.api.Service.EmpresaService;
import com.vulpix.api.dto.Agent.PublicacaoGeradaResponse;
import com.vulpix.api.dto.Empresa.FormularioRequisicaoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class CriativosService {

    private String URL = "http://192.168.0.7:5000/generate-content";
    @Autowired
    private RestTemplate restTemplate;

    public PublicacaoGeradaResponse buscaCriativos(String prompt, String userRequest) {
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

        return responseEntity.getBody();
    }
}
