package com.vulpix.api.Controller;

import com.vulpix.api.Service.Usuario.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("status-pagamentos")
public class PagamentoStatusController {
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("{userId}")
    public ResponseEntity<Boolean> getPaymentStatus(@PathVariable UUID userId) {
        boolean isPaid = usuarioService.verificarPagamento(userId);
        return ResponseEntity.status(200).body(isPaid);
    }
}
