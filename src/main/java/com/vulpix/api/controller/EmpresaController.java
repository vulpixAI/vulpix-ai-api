package com.vulpix.api.controller;

import com.vulpix.api.dto.Empresa.EmpresaEditDto;
import com.vulpix.api.entity.Empresa;
import com.vulpix.api.service.EmpresaService;
import com.vulpix.api.service.usuario.autenticacao.UsuarioAutenticadoUtil;
import com.vulpix.api.dto.Empresa.FormularioRequisicaoDto;
import com.vulpix.api.utils.helpers.EmpresaHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/empresas")
@Tag(name = "Controller de Empresa")
public class EmpresaController {
    @Autowired
    private EmpresaService empresaService;
    @Autowired
    private UsuarioAutenticadoUtil usuarioAutenticadoUtil;

    @Autowired
    private EmpresaHelper empresaHelper;

    @Operation(summary = "Atualiza dados da empresa do usuário autenticado",
            description = "Atualiza os dados da empresa associada ao usuário atualmente autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Empresa atualizada com sucesso.",
                    content = @Content(mediaType = "application/json", examples =
                    @ExampleObject(value = """
                {
                    "id": "a7e9b0de-71a7-4f1e-b8c9-60e16d047d7b",
                    "razaoSocial": "Empresa Exemplo LTDA",
                    "nomeFantasia": "Empresa Exemplo",
                    "cnpj": "12.345.678/0001-99",
                    "cep": "12345-678",
                    "logradouro": "Rua Exemplo",
                    "numero": "100",
                    "bairro": "Centro",
                    "complemento": "Sala 101",
                    "cidade": "São Paulo",
                    "estado": "SP",
                    "created_at": "2023-01-01T10:00:00",
                    "updated_at": "2024-11-03T15:30:00",
                    "usuario": {
                        "id": "98765432-aaaa-bbbb-cccc-1234567890ab",
                        "nome": "João da Silva",
                        "email": "joao@example.com"
                    },
                    "integracoes": [
                        {
                            "id": "12345678-aaaa-bbbb-cccc-1234567890ab",
                            "nome": "Integração 1",
                            "tipo": "Tipo A",
                            "dataCriacao": "2023-05-10T08:45:00"
                        },
                        {
                            "id": "87654321-bbbb-cccc-dddd-0987654321ba",
                            "nome": "Integração 2",
                            "tipo": "Tipo B",
                            "dataCriacao": "2023-06-15T11:20:00"
                        }
                    ]
                }
                """))),
            @ApiResponse(responseCode = "404", description = "Empresa não encontrada.",
                    content = @Content(mediaType = "application/json", examples =
                    @ExampleObject(value = "{ \"message\": \"Erro: Empresa não encontrada.\" }")))
    })
    @PatchMapping
    public ResponseEntity<EmpresaEditDto> atualizar(@RequestBody EmpresaEditDto empresaAtualizada) {
        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String emailUsuario = userDetails.getUsername();
        Empresa empresa = empresaHelper.buscarEmpresaPeloUsuario(emailUsuario);

        if (empresa == null) {
            return ResponseEntity.status(404).build();
        }

        EmpresaEditDto empresaAtualizadaSalva = empresaService.atualizarEmpresa(empresa, empresaAtualizada);
        return ResponseEntity.status(200).body(empresaAtualizadaSalva);
    }


    @Operation(summary = "Cadastra um novo formulário para a empresa do usuário autenticado",
            description = "Adiciona um novo formulário associado à empresa do usuário autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Formulário cadastrado com sucesso.",
                    content = @Content(mediaType = "application/json", examples =
                    @ExampleObject(value = """
                            {
                                "formularioId": "12345",
                                "slogan": "Inovação em cada passo",
                                "descricao": "Empresa focada em soluções tecnológicas avançadas.",
                                "setor": "Tecnologia",
                                "anoFundacao": "2010",
                                "logotipo": "https://link-para-logotipo.com/imagem.png",
                                "corPrimaria": "#0000FF",
                                "corSecundaria": "#FFFFFF",
                                "fonte": "Arial",
                                "estiloVisual": "Moderno e minimalista",
                                "publicoAlvo": "Empresas de médio porte",
                                "problemasQueResolve": "Automação de processos, aumento de eficiência",
                                "expectativaDoCliente": "Soluções rápidas e confiáveis",
                                "produtoEmpresa": "SaaS para gestão empresarial",
                                "diferencialSolucao": "Integração com IA para automação",
                                "concorrentes": "Empresa A, Empresa B",
                                "pontosFortes": "Suporte técnico especializado",
                                "desafiosEnfrentados": "Aumentar o reconhecimento de marca",
                                "redesSociais": "LinkedIn, Facebook",
                                "tonalidadeComunicacao": "Formal e inspirador",
                                "tiposConteudo": "Blog posts, webinars",
                                "objetivoMarketing": "Aumentar a base de clientes",
                                "resultadosEsperados": "Crescimento de 20% em um ano",
                                "datasImportantes": "Aniversário da empresa em março",
                                "estiloCriativos": "Infográficos, tutoriais em vídeo",
                                "referenciasVisuais": "https://link-para-referencia.com/exemplo.png",
                                "observacoesGerais": "Considerar expansão internacional"
                            }
                            """))),
            @ApiResponse(responseCode = "404", description = "Empresa não encontrada.",
                    content = @Content(mediaType = "application/json", examples =
                    @ExampleObject(value = "{ \"message\": \"Erro: Empresa não encontrada.\" }")))
    })
    @PostMapping("/form")
    public ResponseEntity<FormularioRequisicaoDto> cadastrarFormulario(@RequestBody FormularioRequisicaoDto formulario) {
        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String emailUsuario = userDetails.getUsername();
        Empresa empresa = empresaHelper.buscarEmpresaPeloUsuario(emailUsuario);

        if (empresa == null) {
            return ResponseEntity.status(404).build();
        }

        FormularioRequisicaoDto retorno = empresaService.cadastrarFormulario(empresa, formulario);
        if (retorno == null) return ResponseEntity.status(409).build();
        return ResponseEntity.status(201).body(retorno);
    }


    @Operation(summary = "Busca o formulário da empresa do usuário autenticado",
            description = "Retorna o formulário associado à empresa do usuário autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Formulário encontrado com sucesso.",
                    content = @Content(mediaType = "application/json", examples =
                    @ExampleObject(value = """
                            {
                                "slogan": "Inovação em cada passo",
                                "descricao": "Empresa focada em soluções tecnológicas avançadas.",
                                "setor": "Tecnologia",
                                "anoFundacao": "2010",
                                "logotipo": "https://link-para-logotipo.com/imagem.png",
                                "corPrimaria": "#0000FF",
                                "corSecundaria": "#FFFFFF",
                                "fonte": "Arial",
                                "estiloVisual": "Moderno e minimalista",
                                "publicoAlvo": "Empresas de médio porte",
                                "problemasQueResolve": "Automação de processos, aumento de eficiência",
                                "expectativaDoCliente": "Soluções rápidas e confiáveis",
                                "produtoEmpresa": "SaaS para gestão empresarial",
                                "diferencialSolucao": "Integração com IA para automação",
                                "concorrentes": "Empresa A, Empresa B",
                                "pontosFortes": "Suporte técnico especializado",
                                "desafiosEnfrentados": "Aumentar o reconhecimento de marca",
                                "redesSociais": "LinkedIn, Facebook",
                                "tonalidadeComunicacao": "Formal e inspirador",
                                "tiposConteudo": "Blog posts, webinars",
                                "objetivoMarketing": "Aumentar a base de clientes",
                                "resultadosEsperados": "Crescimento de 20% em um ano",
                                "datasImportantes": "Aniversário da empresa em março",
                                "estiloCriativos": "Infográficos, tutoriais em vídeo",
                                "referenciasVisuais": "https://link-para-referencia.com/exemplo.png",
                                "observacoesGerais": "Considerar expansão internacional"
                            }
                            """))),
            @ApiResponse(responseCode = "404", description = "Formulário não encontrado.",
                    content = @Content(mediaType = "application/json", examples =
                    @ExampleObject(value = "{ \"message\": \"Erro: Formulário não encontrado.\" }")))
    })
    @GetMapping("/form")
    public ResponseEntity<FormularioRequisicaoDto> buscaFormulario() {
        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String emailUsuario = userDetails.getUsername();
        Empresa empresa = empresaHelper.buscarEmpresaPeloUsuario(emailUsuario);

        FormularioRequisicaoDto formularioResponse = empresaService.buscaFormulario(empresa);

        if (formularioResponse == null) {
            return ResponseEntity.status(404).build();
        }
        return ResponseEntity.status(200).body(formularioResponse);
    }

    @Operation(summary = "Atualiza o formulário da empresa do usuário autenticado",
            description = "Atualiza os dados do formulário associado à empresa do usuário autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Formulário atualizado com sucesso.",
                    content = @Content(mediaType = "application/json", examples =
                    @ExampleObject(value = """
                            {
                                "slogan": "Inovação em cada passo",
                                "descricao": "Empresa focada em soluções tecnológicas avançadas.",
                                "setor": "Tecnologia",
                                "anoFundacao": "2010",
                                "logotipo": "https://link-para-logotipo.com/imagem.png",
                                "corPrimaria": "#0000FF",
                                "corSecundaria": "#FFFFFF",
                                "fonte": "Arial",
                                "estiloVisual": "Moderno e minimalista",
                                "publicoAlvo": "Empresas de médio porte",
                                "problemasQueResolve": "Automação de processos, aumento de eficiência",
                                "expectativaDoCliente": "Soluções rápidas e confiáveis",
                                "produtoEmpresa": "SaaS para gestão empresarial",
                                "diferencialSolucao": "Integração com IA para automação",
                                "concorrentes": "Empresa A, Empresa B",
                                "pontosFortes": "Suporte técnico especializado",
                                "desafiosEnfrentados": "Aumentar o reconhecimento de marca",
                                "redesSociais": "LinkedIn, Facebook",
                                "tonalidadeComunicacao": "Formal e inspirador",
                                "tiposConteudo": "Blog posts, webinars",
                                "objetivoMarketing": "Aumentar a base de clientes",
                                "resultadosEsperados": "Crescimento de 20% em um ano",
                                "datasImportantes": "Aniversário da empresa em março",
                                "estiloCriativos": "Infográficos, tutoriais em vídeo",
                                "referenciasVisuais": "https://link-para-referencia.com/exemplo.png",
                                "observacoesGerais": "Considerar expansão internacional"
                            }
                            """))),
            @ApiResponse(responseCode = "404", description = "Empresa não encontrada.",
                    content = @Content(mediaType = "application/json", examples =
                    @ExampleObject(value = "{ \"message\": \"Erro: Empresa não encontrada.\" }")))
    })
    @PutMapping("/form")
    public ResponseEntity<FormularioRequisicaoDto> atualizaFormulario(@RequestBody FormularioRequisicaoDto formulario) {
        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String emailUsuario = userDetails.getUsername();
        Empresa empresa = empresaHelper.buscarEmpresaPeloUsuario(emailUsuario);

        if (empresa == null) {
            return ResponseEntity.status(404).build();
        }

        FormularioRequisicaoDto retorno = empresaService.atualizaFormulario(empresa, formulario);
        return ResponseEntity.status(200).body(retorno);
    }
}