package com.vulpix.api.controller;

import com.vulpix.api.dto.CadastroInicial.CadastroRequisicaoDto;
import com.vulpix.api.dto.CadastroInicial.CadastroRequisicaoMapper;
import com.vulpix.api.dto.CadastroInicial.CadastroRetornoDto;
import com.vulpix.api.dto.Usuario.GetUsuarioDto;
import com.vulpix.api.entity.Empresa;
import com.vulpix.api.entity.Usuario;
import com.vulpix.api.services.EmpresaService;
import com.vulpix.api.services.UsuarioService;
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

        if (empresaSalva == null) return ResponseEntity.status(401).build();

        CadastroRetornoDto retorno = CadastroRequisicaoMapper.retornoCadastro(usuarioSalvo, empresaSalva);

        return ResponseEntity.status(201).body(retorno);
    }

    @PostMapping("/login")
    public ResponseEntity<GetUsuarioDto> autenticar(@RequestBody Usuario usuario) {
        Optional<GetUsuarioDto> usuarioRetorno = usuarioService.autenticarUsuario(usuario.getEmail(), usuario.getSenha());
        if (usuarioRetorno.isPresent()) {
            return ResponseEntity.ok(usuarioRetorno.get());
        } else {
            return ResponseEntity.status(401).build();
        }
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