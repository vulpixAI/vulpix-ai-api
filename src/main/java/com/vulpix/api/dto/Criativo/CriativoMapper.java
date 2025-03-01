package com.vulpix.api.dto.Criativo;

import com.vulpix.api.entity.Criativo;
import com.vulpix.api.entity.Empresa;

import java.time.LocalDateTime;

public class CriativoMapper {
    public static Criativo criaEntidadeCriativo(CriativoRequisicaoDto dto, Empresa empresa) {
        if (dto == null) return null;

        Criativo criativo = Criativo.builder()
                .imageUrl(dto.getImageUrl())
                .prompt(dto.getPrompt())
                .createdAt(LocalDateTime.now())
                .empresa(empresa)
                .build();

        return criativo;
    }
}
