package com.vulpix.api.Controller;

import com.vulpix.api.Dto.Criativo.CriativoRequisicaoDto;
import com.vulpix.api.Dto.Criativo.CriativoResponseDto;
import com.vulpix.api.Entity.Empresa;
import com.vulpix.api.Service.Integracoes.AgentAi.CriativosService;
import com.vulpix.api.Service.Usuario.Autenticacao.UsuarioAutenticadoUtil;
import com.vulpix.api.Utils.Helpers.EmpresaHelper;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/criativos")
@Tag(name = "Controller de Criativos")
public class CriativoController {
    @Autowired
    private UsuarioAutenticadoUtil usuarioAutenticadoUtil;
    @Autowired
    private EmpresaHelper empresaHelper;
    @Autowired
    private CriativosService criativosService;

    @GetMapping

    public ResponseEntity<Page<CriativoResponseDto>> buscarCriativos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String dataInicio,
            @RequestParam(required = false) String dataFim

    ) {
        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String emailUsuario = userDetails.getUsername();
        Empresa empresa = empresaHelper.buscarEmpresaPeloUsuario(emailUsuario);

        if (empresa == null) return ResponseEntity.status(404).build();

        Page<CriativoResponseDto> criativos = criativosService.buscaCriativosGerados(empresa, page, size, dataInicio, dataFim);

        if (criativos == null) return ResponseEntity.status(404).build();
        if (criativos.isEmpty()) return ResponseEntity.status(204).build();
        return ResponseEntity.status(200).body(criativos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CriativoRequisicaoDto> buscaCriativoPorId(@PathVariable UUID id) {
        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String emailUsuario = userDetails.getUsername();
        Empresa empresa = empresaHelper.buscarEmpresaPeloUsuario(emailUsuario);

        if (empresa == null) return ResponseEntity.status(404).build();

        CriativoRequisicaoDto response = criativosService.buscaPorId(id);
        if (response == null) return ResponseEntity.status(404).build();
        return ResponseEntity.status(200).body(response);
    }
}
