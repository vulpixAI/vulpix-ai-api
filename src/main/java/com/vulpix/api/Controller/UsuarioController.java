package com.vulpix.api.Controller;

import com.vulpix.api.Dto.CadastroInicial.CadastroRequisicaoDto;
import com.vulpix.api.Dto.CadastroInicial.CadastroRequisicaoMapper;
import com.vulpix.api.Dto.CadastroInicial.CadastroRetornoDto;
import com.vulpix.api.Entity.Empresa;
import com.vulpix.api.Entity.Usuario;
import com.vulpix.api.Services.EmpresaService;
import com.vulpix.api.Services.Usuario.Autenticacao.Dto.UsuarioLoginDto;
import com.vulpix.api.Services.Usuario.Autenticacao.Dto.UsuarioTokenDto;
import com.vulpix.api.Services.Usuario.UsuarioService;
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

    @PostMapping("/login")
    public ResponseEntity<UsuarioTokenDto> autenticar(@RequestBody UsuarioLoginDto usuario) {
        UsuarioTokenDto usuarioRetorno = usuarioService.autenticarUsuario(usuario);
        return ResponseEntity.status(200).body(usuarioRetorno);
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        List<Usuario> usuarios = usuarioService.listarUsuarios();
        if (usuarios.isEmpty()) {
            return ResponseEntity.status(204).build();
        }
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarUsuarioPorId(@PathVariable UUID id) {
        Optional<Usuario> usuarioOpt = usuarioService.buscarUsuarioPorId(id);
        if (usuarioOpt.isPresent()) {
            return ResponseEntity.ok(usuarioOpt.get());
        } else {
            return ResponseEntity.status(404).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizar(@PathVariable UUID id, @RequestBody Usuario usuarioAtualizado) {
        Optional<Usuario> usuarioOpt = usuarioService.atualizarUsuario(id, usuarioAtualizado);
        if (usuarioOpt.isPresent()) {
            return ResponseEntity.ok(usuarioOpt.get());
        } else {
            return ResponseEntity.status(404).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        boolean deletado = usuarioService.deletarUsuario(id);
        if (deletado) {
            return ResponseEntity.status(204).build();
        } else {
            return ResponseEntity.status(404).build();
        }
    }

}