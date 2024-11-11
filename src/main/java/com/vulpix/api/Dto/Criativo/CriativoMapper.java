package com.vulpix.api.Dto.Criativo;

import com.vulpix.api.Entity.Criativo;
import com.vulpix.api.Entity.Empresa;

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
