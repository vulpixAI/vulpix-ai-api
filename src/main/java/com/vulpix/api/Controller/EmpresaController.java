package com.vulpix.api.Controller;

import com.vulpix.api.Entity.Empresa;
import com.vulpix.api.Services.EmpresaService;
import com.vulpix.api.Services.Usuario.Autenticacao.UsuarioAutenticadoUtil;
import com.vulpix.api.dto.Empresa.FormularioRequisicaoDto;
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

    @PatchMapping("")
    public ResponseEntity<Empresa> atualizar(@RequestBody Empresa empresaAtualizada) {
        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String emailUsuario = userDetails.getUsername();
        Empresa empresa = empresaService.buscarEmpresaPeloUsuario(emailUsuario);
        Empresa empresaAtualizadaSalva = empresaService.atualizarEmpresa(empresa,empresaAtualizada);
        if (empresaAtualizadaSalva == null) {
            return ResponseEntity.status(404).build();
        }
        return ResponseEntity.ok(empresaAtualizadaSalva);
    }

    @PostMapping("/form")
    public ResponseEntity<FormularioRequisicaoDto> cadastrarFormulario(@RequestBody FormularioRequisicaoDto formulario) {
        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String emailUsuario = userDetails.getUsername();
        Empresa empresa = empresaService.buscarEmpresaPeloUsuario(emailUsuario);

        if (empresa == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        FormularioRequisicaoDto retorno = empresaService.cadastrarFormulario(empresa, formulario);
        return ResponseEntity.ok().body(retorno);
    }

    @GetMapping("/form")
    public ResponseEntity<FormularioRequisicaoDto> buscaFormulario(){
        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String emailUsuario = userDetails.getUsername();
        Empresa empresa = empresaService.buscarEmpresaPeloUsuario(emailUsuario);

        FormularioRequisicaoDto formularioResponse = empresaService.buscaFormulario(empresa);

        if (formularioResponse == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok().body(formularioResponse);
    }
}