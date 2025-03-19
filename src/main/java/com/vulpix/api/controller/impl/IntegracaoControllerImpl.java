package com.vulpix.api.controller.impl;

import com.vulpix.api.controller.IntegracaoController;
import com.vulpix.api.dto.Integracao.IntegracaoDto;
import com.vulpix.api.dto.Integracao.IntegracaoMapper;
import com.vulpix.api.dto.Integracao.IntegracaoUpdateDto;
import com.vulpix.api.entity.Empresa;
import com.vulpix.api.entity.Integracao;
import com.vulpix.api.service.EmpresaService;
import com.vulpix.api.service.IntegracaoService;
import com.vulpix.api.service.usuario.autenticacao.UsuarioAutenticadoUtil;
import com.vulpix.api.utils.enums.TipoIntegracao;
import com.vulpix.api.utils.helpers.EmpresaHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IntegracaoControllerImpl implements IntegracaoController {
    @Autowired
    private IntegracaoService integracaoService;

    @Autowired
    private UsuarioAutenticadoUtil usuarioAutenticadoUtil;

    @Autowired
    private EmpresaService empresaService;

    @Autowired
    private EmpresaHelper empresaHelper;

    @Override
    public ResponseEntity<Integracao> habilitar(@RequestBody IntegracaoDto novaIntegracao) {
        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String emailUsuario = userDetails.getUsername();
        Empresa empresa = empresaHelper.buscarEmpresaPeloUsuario(emailUsuario);

        Integracao integracao = IntegracaoMapper.criaEntidadeIntegracao(novaIntegracao, empresa);
        Integracao integracaoSalva = integracaoService.cadastrarIntegracao(integracao, empresa);

        return ResponseEntity.status(201).body(integracaoSalva);
    }

    @Override
    public ResponseEntity<Integracao> atualizar(@RequestBody IntegracaoUpdateDto integracaoAtualizada) {
        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String emailUsuario = userDetails.getUsername();
        Empresa empresa = empresaHelper.buscarEmpresaPeloUsuario(emailUsuario);

        Integracao integracaoExistente = integracaoService.buscaIntegracaoPorTipo(empresa, TipoIntegracao.INSTAGRAM);
        Integracao integracao = IntegracaoMapper.criaEntidadeAtualizada(empresa, integracaoAtualizada);

        Integracao integracaoAtualizadaSalva = integracaoService.atualizaIntegracao(integracaoExistente.getId(), integracao);
        return ResponseEntity.status(200).body(integracaoAtualizadaSalva);
    }

    @Override
    public ResponseEntity<Void> deletar() {
        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String emailUsuario = userDetails.getUsername();
        Empresa empresa = empresaHelper.buscarEmpresaPeloUsuario(emailUsuario);

        Integracao integracaoExistente = integracaoService.buscaIntegracaoPorTipo(empresa, TipoIntegracao.INSTAGRAM);

        integracaoService.excluirIntegracao(integracaoExistente.getId());
        return ResponseEntity.status(204).build();
    }

    @Override
    public boolean possuiIntegracao() {
        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String emailUsuario = userDetails.getUsername();
        Empresa empresa = empresaHelper.buscarEmpresaPeloUsuario(emailUsuario);

        return integracaoService.verificaExistenciaIntegracaoPorTipo(empresa, TipoIntegracao.INSTAGRAM);
    }

    @GetMapping
    public ResponseEntity<Integracao> retornaIntegracao() {
        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String emailUsuario = userDetails.getUsername();
        Empresa empresa = empresaHelper.buscarEmpresaPeloUsuario(emailUsuario);

        Integracao retorno = integracaoService.retornaIntegracao(empresa);

        return ResponseEntity.status(200).body(retorno);
    }
}