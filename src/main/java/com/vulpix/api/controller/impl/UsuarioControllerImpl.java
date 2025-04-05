package com.vulpix.api.controller.impl;

import com.vulpix.api.controller.UsuarioController;
import com.vulpix.api.dto.autenticacao.LoginResponse;
import com.vulpix.api.dto.autenticacao.MfaLoginDto;
import com.vulpix.api.dto.autenticacao.MfaRequiredResponse;
import com.vulpix.api.dto.cadastroinicial.CadastroRequisicaoDto;
import com.vulpix.api.dto.cadastroinicial.CadastroRequisicaoMapper;
import com.vulpix.api.dto.cadastroinicial.CadastroRetornoDto;
import com.vulpix.api.dto.usuario.AtualizarSenhaDto;
import com.vulpix.api.dto.usuario.UsuarioEmpresaDto;
import com.vulpix.api.dto.usuario.UsuarioEmpresaMapper;
import com.vulpix.api.entity.Empresa;
import com.vulpix.api.entity.Usuario;
import com.vulpix.api.service.EmpresaService;
import com.vulpix.api.service.GoogleAuthService;
import com.vulpix.api.service.usuario.UsuarioService;
import com.vulpix.api.service.usuario.autenticacao.UsuarioAutenticadoUtil;
import com.vulpix.api.dto.usuario.UsuarioLoginDto;
import com.vulpix.api.dto.autenticacao.UsuarioTokenDto;
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

    @Autowired
    GoogleAuthService googleAuthService;

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
    public ResponseEntity<LoginResponse> autenticar(@RequestBody UsuarioLoginDto usuario) {
        LoginResponse response = usuarioService.autenticarUsuario(usuario);

        if (response instanceof MfaRequiredResponse) {
            return ResponseEntity.status(202).body(response); // MFA Requerido
        }

        return ResponseEntity.ok(response); // MFA OK
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

    @Override
    public ResponseEntity<UsuarioTokenDto> autenticarComOtp(@RequestBody MfaLoginDto mfaLoginDto) {
        UsuarioTokenDto dto = googleAuthService.autenticarComMfa(mfaLoginDto);
        return ResponseEntity.ok(dto);
    }

}