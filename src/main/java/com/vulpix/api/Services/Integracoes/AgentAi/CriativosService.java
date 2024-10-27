package com.vulpix.api.Services.Integracoes.AgentAi;

import com.vulpix.api.Entity.Empresa;
import com.vulpix.api.Services.EmpresaService;
import org.springframework.beans.factory.annotation.Autowired;

public class CriativosService {
    // Aqui vamos enviar uma requisição get para nossa aplicação VulpixAi Agent responsável por gerar os criativos

    @Autowired
    private EmpresaService empresaService;
    public String buscaForm(Empresa empresa) {
        String form = empresaService.buscaFormulario(empresa);
        return form;
    }

    public String buscaCriativos(Empresa empresa) {
        String form = buscaForm(empresa);

        String urlImages = "Requisicao get";

        return urlImages;
    }
}
