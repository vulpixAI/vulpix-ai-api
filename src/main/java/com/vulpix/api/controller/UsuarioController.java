package com.vulpix.api.controller;

import com.vulpix.api.dto.GetUsuarioDto;
import com.vulpix.api.entity.Integracao;
import com.vulpix.api.entity.Usuario;
import com.vulpix.api.repository.UsuarioRepository;
import com.vulpix.api.services.IntegracaoService;
import com.vulpix.api.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/usuarios")

public class UsuarioController {
    @Autowired
    private UsuarioRepository usuarioRepository;
//    private SecurityConfig securityConfig;

    @Autowired
    private UsuarioService usuarioService;
    @PostMapping("/signup")
    public ResponseEntity<GetUsuarioDto> cadastrar(
            @RequestBody Usuario novoUsuario
    ) {
        Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(novoUsuario.getEmail());
        if (usuarioExistente.isPresent()) {
            return ResponseEntity.status(409).build();
        }
        novoUsuario.setId(null);
        novoUsuario.setCreated_at(LocalDateTime.now());
        novoUsuario.setAtivo(true);
        usuarioRepository.save(novoUsuario);

        GetUsuarioDto usuarioRetorno = usuarioService.montaRetornoUsuario(novoUsuario);

        return ResponseEntity.status(201).body(usuarioRetorno);
    }

    @PostMapping("/login")
    public ResponseEntity<GetUsuarioDto> autenticar(
            @RequestBody Usuario usuario) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmailAndSenha(usuario.getEmail(), usuario.getSenha());

        if (!usuarioOpt.isPresent()) {
            return ResponseEntity.status(401).build();
        }
        Usuario usuarioLogin = usuarioOpt.get();

        GetUsuarioDto usuarioRetorno = usuarioService.montaRetornoUsuario(usuarioLogin);

        return ResponseEntity.status(200).body(usuarioRetorno);
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        if (usuarios.isEmpty()) {
            return ResponseEntity.status(204).build();
        }
        return ResponseEntity.status(200).body(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Usuario>> buscarUsuariosPorId(
            @PathVariable UUID id) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isPresent()) {
            Usuario usuarioEncontrado = usuarioOpt.get();
            return ResponseEntity.status(200).body(usuarioOpt);
        }
        return ResponseEntity.status(404).build();
    }


    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizar(
            @PathVariable UUID id,
            @RequestBody Usuario usuarioAtualizado) {
        if (usuarioRepository.existsById(id)) {
            usuarioAtualizado.setId(id);
            return ResponseEntity.status(200).body(usuarioRepository.save(usuarioAtualizado));
        }
        return ResponseEntity.status(404).build();
    }

//    @PatchMapping("/update-password/{id}")
//    public ResponseEntity<Optional<Usuario>> atualizarSenha(
//            @PathVariable int id,
//            @RequestBody String novaSenha) {
//        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
//        if (usuarioOpt.isPresent()) {
//            Usuario usuario = usuarioOpt.get();
//            String senhaCodificada = securityConfig.criptografarSenhas().encode(novaSenha);
//            usuario.setSenha(senhaCodificada);
//            usuarioRepository.save(usuario);
//            return ResponseEntity.status(200).body(usuarioOpt);
//        }
//        return ResponseEntity.status(404).build();
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable UUID id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return ResponseEntity.status(204).build();
        }
        return ResponseEntity.status(404).build();
    }


}
