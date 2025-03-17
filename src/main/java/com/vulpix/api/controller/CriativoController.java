package com.vulpix.api.controller;

import com.vulpix.api.api.CriativoApi;
import com.vulpix.api.dto.Criativo.CriativoRequisicaoDto;
import com.vulpix.api.dto.Criativo.CriativoResponseDto;
import com.vulpix.api.entity.Empresa;
import com.vulpix.api.service.integracoes.agentai.CriativosService;
import com.vulpix.api.service.usuario.autenticacao.UsuarioAutenticadoUtil;
import com.vulpix.api.utils.helpers.EmpresaHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class CriativoController implements CriativoApi {
    @Autowired
    private UsuarioAutenticadoUtil usuarioAutenticadoUtil;

    @Autowired
    private EmpresaHelper empresaHelper;

    @Autowired
    private CriativosService criativosService;

    @Override
    public ResponseEntity<Page<CriativoResponseDto>> buscarCriativos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String dataInicio,
            @RequestParam(required = false) String dataFim
    ) {
        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String emailUsuario = userDetails.getUsername();
        Empresa empresa = empresaHelper.buscarEmpresaPeloUsuario(emailUsuario);

        Page<CriativoResponseDto> criativos = criativosService.buscaCriativosGerados(empresa, page, size, dataInicio, dataFim);

        if (criativos.isEmpty()) {
            return ResponseEntity.status(204).build();
        }

        return ResponseEntity.status(200).body(criativos);
    }

    @Override
    public ResponseEntity<CriativoRequisicaoDto> buscaCriativoPorId(@PathVariable UUID id) {
        CriativoRequisicaoDto response = criativosService.buscaPorId(id);
        return ResponseEntity.status(200).body(response);
    }
}