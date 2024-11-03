package com.vulpix.api.Dto.Empresa;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FormularioRequisicaoDto {
    private String slogan;
    @NotBlank
    private String descricao;
    @NotBlank
    private String setor;
    @NotBlank
    private String anoFundacao;
    @NotBlank
    private String logotipo;
    @NotBlank
    private String corPrimaria;
    @NotBlank
    private String corSecundaria;
    @NotBlank
    private String fonte;
    @NotBlank
    private String estiloVisual;
    @NotBlank
    private String publicoAlvo;
    @NotBlank
    private String problemasQueResolve;
    @NotBlank
    private String expectativaDoCliente;
    @NotBlank
    private String produtoEmpresa;
    @NotBlank
    private String diferencialSolucao;
    @NotBlank
    private String concorrentes;
    @NotBlank
    private String pontosFortes;
    @NotBlank
    private String desafiosEnfrentados;
    @NotBlank
    private String redesSociais;
    @NotBlank
    private String tonalidadeComunicacao;
    @NotBlank
    private String tiposConteudo;
    @NotBlank
    private String objetivoMarketing;
    @NotBlank
    private String resultadosEsperados;
    @NotBlank
    private String datasImportantes;
    @NotBlank
    private String estiloCriativos;
    @NotBlank
    private String referenciasVisuais;
    private String observacoesGerais;
}
