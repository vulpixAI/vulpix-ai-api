package com.vulpix.api.controller.impl;

import com.vulpix.api.controller.UsuarioController;
import com.vulpix.api.dto.CadastroInicial.CadastroRequisicaoDto;
import com.vulpix.api.dto.CadastroInicial.CadastroRequisicaoMapper;
import com.vulpix.api.dto.CadastroInicial.CadastroRetornoDto;
import com.vulpix.api.dto.Usuario.AtualizarSenhaDto;
import com.vulpix.api.dto.Usuario.UsuarioEmpresaDto;
import com.vulpix.api.dto.Usuario.UsuarioEmpresaMapper;
import com.vulpix.api.entity.Empresa;
import com.vulpix.api.entity.Usuario;
import com.vulpix.api.service.EmpresaService;
import com.vulpix.api.service.usuario.UsuarioService;
import com.vulpix.api.service.usuario.autenticacao.UsuarioAutenticadoUtil;
import com.vulpix.api.service.usuario.autenticacao.dto.UsuarioLoginDto;
import com.vulpix.api.service.usuario.autenticacao.dto.UsuarioTokenDto;
import com.vulpix.api.utils.helpers.EmpresaHelper;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class UsuarioControllerImpl implements UsuarioController {
    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private EmpresaService empresaService;

    @Autowired
    private UsuarioAutenticadoUtil usuarioAutenticadoUtil;

    @Autowired
    private EmpresaHelper empresaHelper;

    @Override
    public ResponseEntity<CadastroRetornoDto> cadastrar(@RequestBody CadastroRequisicaoDto cadastroRequisicaoDto) {
        Usuario usuarioEntidade = CadastroRequisicaoMapper.criaEntidadeUsuario(cadastroRequisicaoDto);
        Usuario usuarioSalvo = usuarioService.cadastrarUsuario(usuarioEntidade, cadastroRequisicaoDto.getCnpj());

        Empresa empresaEntidade = CadastroRequisicaoMapper.criaEntidadeEmpresa(cadastroRequisicaoDto, usuarioSalvo);
        Empresa empresaSalva = empresaService.salvarEmpresa(empresaEntidade);

        CadastroRetornoDto retorno = CadastroRequisicaoMapper.retornoCadastro(usuarioSalvo, empresaSalva);
        return ResponseEntity.status(201).body(retorno);
    }

    @Override
    public ResponseEntity<UsuarioTokenDto> autenticar(@RequestBody UsuarioLoginDto usuario) {
        UsuarioTokenDto usuarioRetorno = usuarioService.autenticarUsuario(usuario);
        return ResponseEntity.status(200).body(usuarioRetorno);
    }

    @Override
    public ResponseEntity<UsuarioEmpresaDto> buscarUsuarioComEmpresa() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String emailUsuario = userDetails.getUsername();

        Usuario usuario = usuarioService.buscarUsuarioPorEmail(emailUsuario);
        Empresa empresa = empresaHelper.buscarEmpresaPeloUsuario(usuario.getEmail());
        UsuarioEmpresaDto dto = UsuarioEmpresaMapper.toDto(usuario, empresa);

        return ResponseEntity.status(200).body(dto);
    }

    @Override
    public ResponseEntity<Usuario> atualizar(@RequestBody Usuario usuarioAtualizado) {
        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String emailUsuario = userDetails.getUsername();

        Usuario usuario = usuarioService.buscarUsuarioPorEmail(emailUsuario);
        usuarioService.atualizarUsuario(usuario.getId(), usuarioAtualizado);

        return ResponseEntity.status(200).body(usuario);
    }

    @Override
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

    @Override
    public ResponseEntity<Void> remover(@Parameter(description = "Usuário a ser removido", required = true) @PathVariable UUID id) {
        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String emailUsuario = userDetails.getUsername();

        Usuario usuario = usuarioService.buscarUsuarioPorEmail(emailUsuario);
        usuarioService.deletarUsuario(usuario.getId());

        return ResponseEntity.status(204).build();
    }
}