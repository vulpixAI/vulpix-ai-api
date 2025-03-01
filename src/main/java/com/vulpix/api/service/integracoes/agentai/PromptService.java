package com.vulpix.api.service.integracoes.agentai;

import com.vulpix.api.entity.ConfigPrompt;
import com.vulpix.api.utils.JsonConverter;
import com.vulpix.api.dto.Empresa.FormularioRequisicaoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class PromptService {
    private String URL = "http://127.0.0.1:5000/generate-prompt";

    @Autowired
    private RestTemplate restTemplate;

    public String generatePrompt(FormularioRequisicaoDto formData) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("form_data", formData);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        System.out.println("Requisição: " + requestBody);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(URL, requestEntity, String.class);
            return response.getBody();

        } catch (HttpServerErrorException e) {
            System.err.println("Erro no servidor Python ao gerar prompt: " + e.getMessage());
            return null;
        }
    }

    public String salvarPrompt(ConfigPrompt configPrompt) {
        FormularioRequisicaoDto form = JsonConverter.fromJson(configPrompt.getForm());
        return generatePrompt(form);
    }
}
