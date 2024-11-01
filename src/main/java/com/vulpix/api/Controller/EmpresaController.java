package com.vulpix.api.Controller;

import com.vulpix.api.Entity.Empresa;
import com.vulpix.api.Services.EmpresaService;
import com.vulpix.api.Services.Usuario.Autenticacao.UsuarioAutenticadoUtil;
import com.vulpix.api.dto.Empresa.FormularioRequisicaoDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/empresas")
public class EmpresaController {
    @Autowired
    private EmpresaService empresaService;
    @Autowired
    private UsuarioAutenticadoUtil usuarioAutenticadoUtil;

    @Operation(summary = "Atualiza os dados da empresa",
            description = "Atualiza os dados da empresa associada ao usuário autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Empresa atualizada com sucesso."),
            @ApiResponse(responseCode = "404", description = "Empresa não encontrada.")
    })
    @PatchMapping("")
    public ResponseEntity<Empresa> atualizar(@RequestBody Empresa empresaAtualizada) {
        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String emailUsuario = userDetails.getUsername();
        Empresa empresa = empresaService.buscarEmpresaPeloUsuario(emailUsuario);
        Empresa empresaAtualizadaSalva = empresaService.atualizarEmpresa(empresa, empresaAtualizada);
        if (empresaAtualizadaSalva == null) {
            return ResponseEntity.status(404).build();
        }
        return ResponseEntity.ok(empresaAtualizadaSalva);
    }
    @Operation(summary = "Cadastra um novo formulário",
            description = "Cadastra um formulário associado à empresa do usuário autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Formulário cadastrado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Empresa não encontrada.")
    })
    @PostMapping("/form")
    public ResponseEntity<FormularioRequisicaoDto> cadastrarFormulario(@RequestBody FormularioRequisicaoDto formulario) {
        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String emailUsuario = userDetails.getUsername();
        Empresa empresa = empresaService.buscarEmpresaPeloUsuario(emailUsuario);

        if (empresa == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        FormularioRequisicaoDto retorno = empresaService.cadastrarFormulario(empresa, formulario);
        return ResponseEntity.ok().body(retorno);
    }
    @Operation(summary = "Busca o formulário da empresa",
            description = "Busca o formulário associado à empresa do usuário autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Formulário encontrado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Formulário não encontrado.")
    })
    @GetMapping("/form")
    public ResponseEntity<FormularioRequisicaoDto> buscaFormulario() {
        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String emailUsuario = userDetails.getUsername();
        Empresa empresa = empresaService.buscarEmpresaPeloUsuario(emailUsuario);

        FormularioRequisicaoDto formularioResponse = empresaService.buscaFormulario(empresa);

        if (formularioResponse == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok().body(formularioResponse);
    }

    @PutMapping("/form")
    public ResponseEntity<FormularioRequisicaoDto> atualizaFormulario(@RequestBody FormularioRequisicaoDto formulario) {
        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String emailUsuario = userDetails.getUsername();
        Empresa empresa = empresaService.buscarEmpresaPeloUsuario(emailUsuario);

        if (empresa == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        FormularioRequisicaoDto retorno = empresaService.atualizaFormulario(empresa, formulario);
        return ResponseEntity.ok().body(retorno);
    }
}