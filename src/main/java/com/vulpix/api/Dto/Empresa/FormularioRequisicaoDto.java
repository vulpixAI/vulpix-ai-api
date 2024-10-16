package com.vulpix.api.dto.Empresa;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.*;
import java.io.Serializable;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FormularioRequisicaoDto implements Serializable {
    private String slogan;
    private String descricao;
    private String setor;
    private String anoFundacao;
    private String logotipo;
    private String corPrimaria;
    private String corSecundaria;
    private String fonte;
    private String estiloVisual;
    private String publicoAlvo;
    private String problemasQueResolve;
    private String expectativaDoCliente;
    private String produtoEmpresa;
    private String diferencialSolucao;
    private String concorrentes;
    private String pontosFortes;
    private String desafiosEnfrentados;
    private String redesSociais;
    private String tonalidadeComunicacao;
    private String tiposConteudo;
    private String objetivoMarketing;
    private String resultadosEsperados;
    private String datasImportantes;
    private String estiloCriativos;
    private String referenciasVisuais;
    private String observacoesGerais;
}
