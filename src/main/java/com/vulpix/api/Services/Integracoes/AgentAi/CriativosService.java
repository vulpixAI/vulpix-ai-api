package com.vulpix.api.Services.Integracoes.AgentAi;

import com.vulpix.api.Entity.Empresa;
import com.vulpix.api.Services.EmpresaService;
import com.vulpix.api.dto.Empresa.FormularioRequisicaoDto;
import org.springframework.beans.factory.annotation.Autowired;

public class CriativosService {
    // Aqui vamos enviar uma requisição get para nossa aplicação VulpixAi Agent responsável por gerar os criativos

    @Autowired
    private EmpresaService empresaService;
    public FormularioRequisicaoDto buscaForm(Empresa empresa) {
        FormularioRequisicaoDto form = empresaService.buscaFormulario(empresa);
        return form;
    }

    public String buscaCriativos(Empresa empresa) {
        FormularioRequisicaoDto form = buscaForm(empresa);

        String urlImages = "Requisicao get";

        return urlImages;
    }
}
