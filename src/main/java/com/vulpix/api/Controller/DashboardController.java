package com.vulpix.api.Controller;

import com.vulpix.api.Dto.Criativo.CriativoRequisicaoDto;
import com.vulpix.api.Entity.Empresa;
import com.vulpix.api.Entity.PostInsights;
import com.vulpix.api.Service.DashboardService;
import com.vulpix.api.Service.Usuario.Autenticacao.UsuarioAutenticadoUtil;
import com.vulpix.api.Utils.Helpers.EmpresaHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/dash")
public class DashboardController {
    @Autowired
    private UsuarioAutenticadoUtil usuarioAutenticadoUtil;
    @Autowired
    private EmpresaHelper empresaHelper;
    @Autowired
    private DashboardService dashboardService;
    
    @GetMapping("/grafico-linha")
    public ResponseEntity<List<PostInsights>> buscaUltimaMetricaPost() {
        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String emailUsuario = userDetails.getUsername();
        Empresa empresa = empresaHelper.buscarEmpresaPeloUsuario(emailUsuario);

        if (empresa == null) return ResponseEntity.status(404).build();

        List<PostInsights> response = dashboardService.buscaMetricaUltimoPost(empresa);
        if (response == null) return ResponseEntity.status(204).build();
        return ResponseEntity.status(200).body(response);
    }
}
