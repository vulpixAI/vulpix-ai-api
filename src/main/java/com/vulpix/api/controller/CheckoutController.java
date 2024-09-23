package com.vulpix.api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pagamentos")
public class CheckoutController {

//
//    @PostMapping("/checkout")
//    public ResponseEntity<Map<String, Object>> criarPagamento() {
//        Map<String, Object> resposta = new HashMap<>();
//        String approveUrl;
//
//        try {
//            approveUrl = this.mercadoPagoService.criarPagamento();
//        } catch (DemoException e) {
//            resposta.put("error", e.getMessage());
//            return new ResponseEntity<>(resposta, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//
//        resposta.put("approveUrl", approveUrl);
//        return new ResponseEntity<>(resposta, HttpStatus.CREATED);
//    }

}
