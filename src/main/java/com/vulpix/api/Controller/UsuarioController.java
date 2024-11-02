package com.vulpix.api.Controller;

import com.vulpix.api.Dto.CadastroInicial.CadastroRequisicaoDto;
import com.vulpix.api.Dto.CadastroInicial.CadastroRequisicaoMapper;
import com.vulpix.api.Dto.CadastroInicial.CadastroRetornoDto;
import com.vulpix.api.Dto.Usuario.UsuarioEmpresaDto;
import com.vulpix.api.Dto.Usuario.UsuarioEmpresaMapper;
import com.vulpix.api.Entity.Empresa;
import com.vulpix.api.Entity.Usuario;
import com.vulpix.api.Service.EmpresaService;
import com.vulpix.api.Service.Usuario.Autenticacao.Dto.UsuarioLoginDto;
import com.vulpix.api.Service.Usuario.Autenticacao.Dto.UsuarioTokenDto;
import com.vulpix.api.Service.Usuario.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private EmpresaService empresaService;

    @Operation(summary = "Cadastrar um novo usuário", description = "Realiza o cadastro de um novo usuário e sua empresa associada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário e empresa cadastrados com sucesso."),
            @ApiResponse(responseCode = "409", description = "Conflito ao tentar salvar a empresa.")
    })
    @PostMapping
    public ResponseEntity<CadastroRetornoDto> cadastrar(@RequestBody CadastroRequisicaoDto cadastroInicial) {
        Usuario usuarioEntidade = CadastroRequisicaoMapper.criaEntidadeUsuario(cadastroInicial);
        Usuario usuarioSalvo = usuarioService.cadastrarUsuario(usuarioEntidade);
        Empresa empresaEntidade = CadastroRequisicaoMapper.criaEntidadeEmpresa(cadastroInicial, usuarioSalvo);
        Empresa empresaSalva = empresaService.salvarEmpresa(empresaEntidade);

        if (empresaSalva == null) return ResponseEntity.status(409).build();

        CadastroRetornoDto retorno = CadastroRequisicaoMapper.retornoCadastro(usuarioSalvo, empresaSalva);
        return ResponseEntity.status(201).body(retorno);
    }

    @Operation(summary = "Autenticar um usuário", description = "Realiza a autenticação de um usuário.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário autenticado com sucesso."),
            @ApiResponse(responseCode = "401", description = "Falha na autenticação.")
    })
    @PostMapping("/login")
    public ResponseEntity<UsuarioTokenDto> autenticar(@RequestBody UsuarioLoginDto usuario) {
        UsuarioTokenDto usuarioRetorno = usuarioService.autenticarUsuario(usuario);
        return ResponseEntity.status(200).body(usuarioRetorno);
    }

    @Operation(summary = "Listar usuários", description = "Retorna uma lista de todos os usuários cadastrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso."),
            @ApiResponse(responseCode = "204", description = "Nenhum usuário encontrado.")
    })
    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        List<Usuario> usuarios = usuarioService.listarUsuarios();
        if (usuarios.isEmpty()) {
            return ResponseEntity.status(204).build();
        }
        return ResponseEntity.ok(usuarios);
    }

    @Operation(summary = "Buscar usuário por ID", description = "Retorna um usuário específico com base no ID fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado."),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarUsuarioPorId(@Parameter(description = "ID do usuário a ser buscado", required = true) @PathVariable UUID id) {
        Optional<Usuario> usuarioOpt = usuarioService.buscarUsuarioPorId(id);
        if (usuarioOpt.isPresent()) {
            return ResponseEntity.ok(usuarioOpt.get());
        } else {
            return ResponseEntity.status(404).build();
        }
    }

    @Operation(summary = "Buscar dados do usuário e da empresa associada", description = "Retorna os dados do usuário juntamente com os dados da empresa associada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dados do usuário e empresa retornados com sucesso."),
            @ApiResponse(responseCode = "404", description = "Usuário ou empresa não encontrados.")
    })
    @GetMapping("/{id}/empresa")
    public ResponseEntity<UsuarioEmpresaDto> buscarUsuarioComEmpresa(@Parameter(description = "ID do usuário", required = true) @PathVariable UUID id) {
        Optional<Usuario> usuarioOpt = usuarioService.buscarUsuarioPorId(id);

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(404).build();
        }

        Usuario usuario = usuarioOpt.get();
        Empresa empresa = empresaService.buscarEmpresaPeloUsuario(usuario.getEmail());

        if (empresa == null) {
            return ResponseEntity.status(404).build();
        }

        UsuarioEmpresaDto dto = UsuarioEmpresaMapper.toDto(usuario, empresa);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Atualizar usuário", description = "Atualiza as informações de um usuário com base no ID fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado.")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizar(@Parameter(description = "ID do usuário a ser atualizado", required = true) @PathVariable UUID id, @RequestBody Usuario usuarioAtualizado) {
        Optional<Usuario> usuarioOpt = usuarioService.atualizarUsuario(id, usuarioAtualizado);
        if (usuarioOpt.isPresent()) {
            return ResponseEntity.ok(usuarioOpt.get());
        } else {
            return ResponseEntity.status(404).build();
        }
    }

    @Operation(summary = "Deletar usuário", description = "Remove um usuário com base no ID fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuário deletado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@Parameter(description = "ID do usuário a ser deletado", required = true) @PathVariable UUID id) {
        boolean deletado = usuarioService.deletarUsuario(id);
        if (deletado) {
            return ResponseEntity.status(204).build();
        } else {
            return ResponseEntity.status(404).build();
        }
    }
}