package com.vulpix.api.controller.impl;

import com.vulpix.api.controller.PagamentoController;
import com.vulpix.api.entity.Empresa;
import com.vulpix.api.service.PagamentoService;
import com.vulpix.api.service.usuario.autenticacao.UsuarioAutenticadoUtil;
import com.vulpix.api.utils.helpers.EmpresaHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PagamentoControllerImpl implements PagamentoController {
    @Autowired
    UsuarioAutenticadoUtil usuarioAutenticadoUtil;

    @Autowired
    EmpresaHelper empresaHelper;

    @Autowired
    PagamentoService pagamentoService;

    @Override
    public ResponseEntity<String> pagamento() {
        UserDetails userDetails = usuarioAutenticadoUtil.getUsuarioDetalhes();
        String emailUsuario = userDetails.getUsername();
        Empresa empresa = empresaHelper.buscarEmpresaPeloUsuario(emailUsuario);

        String url = pagamentoService.criarPaymentLink(empresa);
        return ResponseEntity.status(200).body(url);
    }

    @Value("${stripe.chave-webhook}")
    private String endpointSecret;

    @Override
    public ResponseEntity<String> handleWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        pagamentoService.criarWebhook(payload, sigHeader, endpointSecret);
        return ResponseEntity.status(200).build();
    }
}