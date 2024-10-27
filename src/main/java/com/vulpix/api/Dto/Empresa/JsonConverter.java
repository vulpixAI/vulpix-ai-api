package com.vulpix.api.dto.Empresa;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
@Converter(autoApply = true)
public class JsonConverter implements AttributeConverter<FormularioRequisicaoDto, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(FormularioRequisicaoDto attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Erro ao converter o FormularioRequisicaoDto para JSON", e);
        }
    }

    @Override
    public FormularioRequisicaoDto convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, FormularioRequisicaoDto.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Erro ao converter o JSON para FormularioRequisicaoDto", e);
        }
    }
}