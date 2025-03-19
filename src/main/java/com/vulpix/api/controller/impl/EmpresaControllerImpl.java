package com.vulpix.api.controller.impl;

import com.vulpix.api.controller.EmpresaController;
import com.vulpix.api.dto.Empresa.EmpresaEditDto;
import com.vulpix.api.dto.Empresa.FormularioRequisicaoDto;
import com.vulpix.api.entity.Empresa;
import com.vulpix.api.service.EmpresaService;
import com.vulpix.api.service.usuario.autenticacao.UsuarioAutenticadoUtil;
import com.vulpix.api.utils.helpers.EmpresaHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmpresaControllerImpl implements EmpresaController {
    @Autowired
    private EmpresaService empresaService;

    @Autowired
    private UsuarioAutenticadoUtil usuarioAutenticadoUtil;

    @Autowired
    private EmpresaHelper empresaHelper;

    @Override
    public ResponseEntity<EmpresaEditDto> atualizar(@RequestBody EmpresaEditDto empresaAtualizada) {
        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String emailUsuario = userDetails.getUsername();
        Empresa empresa = empresaHelper.buscarEmpresaPeloUsuario(emailUsuario);

        EmpresaEditDto empresaAtualizadaSalva = empresaService.atualizarEmpresa(empresa, empresaAtualizada);
        return ResponseEntity.status(200).body(empresaAtualizadaSalva);
    }

    @Override
    public ResponseEntity<FormularioRequisicaoDto> cadastrarFormulario(@RequestBody FormularioRequisicaoDto formulario) {
        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String emailUsuario = userDetails.getUsername();
        Empresa empresa = empresaHelper.buscarEmpresaPeloUsuario(emailUsuario);

        FormularioRequisicaoDto retorno = empresaService.cadastrarFormulario(empresa, formulario);

        return ResponseEntity.status(201).body(retorno);
    }

    @Override
    public ResponseEntity<FormularioRequisicaoDto> buscaFormulario() {
        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String emailUsuario = userDetails.getUsername();
        Empresa empresa = empresaHelper.buscarEmpresaPeloUsuario(emailUsuario);

        FormularioRequisicaoDto formularioResponse = empresaService.buscaFormulario(empresa);

        return ResponseEntity.status(200).body(formularioResponse);
    }

    @Override
    public ResponseEntity<FormularioRequisicaoDto> atualizaFormulario(@RequestBody FormularioRequisicaoDto formulario) {
        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String emailUsuario = userDetails.getUsername();
        Empresa empresa = empresaHelper.buscarEmpresaPeloUsuario(emailUsuario);

        FormularioRequisicaoDto retorno = empresaService.atualizaFormulario(empresa, formulario);
        return ResponseEntity.status(200).body(retorno);
    }
}