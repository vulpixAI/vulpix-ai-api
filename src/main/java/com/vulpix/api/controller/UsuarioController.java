package com.vulpix.api.controller;

import com.vulpix.api.dto.CadastroInicial.CadastroRequisicaoDto;
import com.vulpix.api.dto.CadastroInicial.CadastroRequisicaoMapper;
import com.vulpix.api.dto.CadastroInicial.CadastroRetornoDto;
import com.vulpix.api.dto.Usuario.AtualizarSenhaDto;
import com.vulpix.api.dto.Usuario.UsuarioEmpresaDto;
import com.vulpix.api.dto.Usuario.UsuarioEmpresaMapper;
import com.vulpix.api.entity.Empresa;
import com.vulpix.api.entity.Usuario;
import com.vulpix.api.service.EmpresaService;
import com.vulpix.api.service.usuario.autenticacao.dto.UsuarioLoginDto;
import com.vulpix.api.service.usuario.autenticacao.dto.UsuarioTokenDto;
import com.vulpix.api.service.usuario.autenticacao.UsuarioAutenticadoUtil;
import com.vulpix.api.service.usuario.UsuarioService;
import com.vulpix.api.utils.helpers.EmpresaHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Controller de Usuário")
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private EmpresaService empresaService;

    @Autowired
    private UsuarioAutenticadoUtil usuarioAutenticadoUtil;

    @Autowired
    private EmpresaHelper empresaHelper;

    @Operation(summary = "Cadastrar um novo usuário", description = "Realiza o cadastro de um novo usuário e sua empresa associada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário e empresa cadastrados com sucesso.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"id\": \"123e4567-e89b-12d3-a456-426614174000\", \"nome\": \"João\", \"empresa\": { \"nome\": \"Empresa Exemplo\" }}"))
            ),
            @ApiResponse(responseCode = "409", description = "Conflito ao tentar salvar a empresa.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"message\": \"Erro: Empresa já cadastrada.\" }"))
            )
    })
    @PostMapping
    public ResponseEntity<CadastroRetornoDto> cadastrar(@RequestBody CadastroRequisicaoDto cadastroRequisicaoDto) {
        Usuario usuarioEntidade = CadastroRequisicaoMapper.criaEntidadeUsuario(cadastroRequisicaoDto);
        Usuario usuarioSalvo = usuarioService.cadastrarUsuario(usuarioEntidade, cadastroRequisicaoDto.getCnpj());

        Empresa empresaEntidade = CadastroRequisicaoMapper.criaEntidadeEmpresa(cadastroRequisicaoDto, usuarioSalvo);
        Empresa empresaSalva = empresaService.salvarEmpresa(empresaEntidade);

        CadastroRetornoDto retorno = CadastroRequisicaoMapper.retornoCadastro(usuarioSalvo, empresaSalva);
        return ResponseEntity.status(201).body(retorno);
    }

    @Operation(summary = "Autenticar um usuário", description = "Realiza a autenticação de um usuário.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário autenticado com sucesso.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"token\": \"abcdefg1234567\", \"usuarioId\": \"123e4567-e89b-12d3-a456-426614174000\" }"))
            ),
            @ApiResponse(responseCode = "401", description = "Falha na autenticação.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"message\": \"Erro: Credenciais inválidas.\" }"))
            )
    })
    @PostMapping("/login")
    public ResponseEntity<UsuarioTokenDto> autenticar(@RequestBody UsuarioLoginDto usuario) {
        UsuarioTokenDto usuarioRetorno = usuarioService.autenticarUsuario(usuario);
        return ResponseEntity.status(200).body(usuarioRetorno);
    }

    @Operation(summary = "Buscar dados do usuário e da empresa associada",
            description = "Retorna os dados do usuário juntamente com os dados da empresa associada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dados do usuário e empresa retornados com sucesso.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"usuario\": { \"id\": \"123e4567-e89b-12d3-a456-426614174000\", \"nome\": \"João\" }, \"empresa\": { \"nome\": \"Empresa Exemplo\" } }"))
            ),
            @ApiResponse(responseCode = "404", description = "Usuário ou empresa não encontrados.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"message\": \"Erro: Usuário ou empresa não encontrados.\" }"))
            )
    })
    @GetMapping
    public ResponseEntity<UsuarioEmpresaDto> buscarUsuarioComEmpresa() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String emailUsuario = userDetails.getUsername();

        Usuario usuario = usuarioService.buscarUsuarioPorEmail(emailUsuario);
        Empresa empresa = empresaHelper.buscarEmpresaPeloUsuario(usuario.getEmail());
        UsuarioEmpresaDto dto = UsuarioEmpresaMapper.toDto(usuario, empresa);

        return ResponseEntity.status(200).body(dto);
    }

    @Operation(summary = "Atualizar usuário", description = "Atualiza as informações de um usuário com base no ID fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"id\": \"123e4567-e89b-12d3-a456-426614174000\", \"nome\": \"João Atualizado\" }"))
            ),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"message\": \"Erro: Usuário não encontrado.\" }"))
            )
    })
    @PutMapping
    public ResponseEntity<Usuario> atualizar(@Parameter(description = "Usuário a ser atualizado", required = true) @RequestBody Usuario usuarioAtualizado) {
        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String emailUsuario = userDetails.getUsername();

        Usuario usuario = usuarioService.buscarUsuarioPorEmail(emailUsuario);
        usuarioService.atualizarUsuario(usuario.getId(), usuarioAtualizado);

        return ResponseEntity.status(200).body(usuario);
    }

    @Operation(
            summary = "Atualizar senha do usuário",
            description = "Atualiza a senha de um usuário autenticado, verificando a senha atual antes de aplicar a nova."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Senha atualizada com sucesso."),
            @ApiResponse(
                    responseCode = "401",
                    description = "Senha atual fornecida está incorreta.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"message\": \"Erro: Senha atual incorreta.\" }"))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário não encontrado.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"message\": \"Erro: Usuário não encontrado.\" }")))
    })
    @PatchMapping("/senha")
    public ResponseEntity<Void> atualizarSenha(@RequestBody AtualizarSenhaDto atualizarSenhaDto) {
        String senhaAtual = atualizarSenhaDto.getSenhaAtual();
        String novaSenha = atualizarSenhaDto.getNovaSenha();

        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String emailUsuario = userDetails.getUsername();

        Usuario usuario = usuarioService.buscarUsuarioPorEmail(emailUsuario);
        usuarioService.verificarSenhaAtual(usuario.getSenha(), senhaAtual);
        usuarioService.atualizarSenha(usuario.getId(), novaSenha);

        return ResponseEntity.status(204).build();
    }

    @Operation(summary = "Remover usuário", description = "Remove um usuário com base no ID fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuário removido com sucesso."),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"message\": \"Erro: Usuário não encontrado.\" }"))
            )
    })
    @DeleteMapping
    public ResponseEntity<Void> remover(@Parameter(description = "Usuário a ser removido", required = true) @PathVariable UUID id) {
        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String emailUsuario = userDetails.getUsername();

        Usuario usuario = usuarioService.buscarUsuarioPorEmail(emailUsuario);
        usuarioService.deletarUsuario(usuario.getId());

        return ResponseEntity.status(204).build();
    }
}