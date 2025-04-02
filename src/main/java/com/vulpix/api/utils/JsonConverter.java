package com.vulpix.api.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vulpix.api.dto.empresa.FormularioRequisicaoDto;

public class JsonConverter {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Método para converter FormularioRequisicaoDto para String JSON
    public static String toJson(FormularioRequisicaoDto formulario) {
        try {
            return objectMapper.writeValueAsString(formulario);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao converter formulário para JSON", e);
        }
    }

    // Método para converter String JSON de volta para FormularioRequisicaoDto
    public static FormularioRequisicaoDto fromJson(String json) {
        try {
            return objectMapper.readValue(json, FormularioRequisicaoDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao converter JSON para formulário", e);
        }
    }
}
