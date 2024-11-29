package com.vulpix.api.Controller;

import com.vulpix.api.Dto.CadastroInicial.CadastroRequisicaoDto;
import com.vulpix.api.Dto.CadastroInicial.CadastroRetornoDto;
import com.vulpix.api.Service.EmpresaService;
import com.vulpix.api.Service.Usuario.UsuarioService;
import com.vulpix.api.Service.Usuario.Autenticacao.Dto.UsuarioTokenDto;
import com.vulpix.api.Entity.Usuario;
import com.vulpix.api.Entity.Empresa;
import com.vulpix.api.Service.Usuario.Autenticacao.UsuarioAutenticadoUtil;
import com.vulpix.api.Utils.Helpers.EmpresaHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Teste da Classe UsuarioController")
class UsuarioControllerTest {

    @InjectMocks
    private UsuarioController usuarioController;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private EmpresaService empresaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Dado que o usuário não existe, quando o usuário é cadastrado, então o usuário é salvo com sucesso.")
    void testCadastrarUsuario_Conflict() {
        CadastroRequisicaoDto cadastroDto = CadastroRequisicaoDto.builder()
                .nome("João")
                .sobrenome("Silva")
                .email("joao.silva@empresa.com")
                .telefone("11987654321")
                .senha("SenhaSegura123!")
                .razaoSocial("Empresa Exemplo Ltda")
                .nomeFantasia("Empresa Exemplo")
                .cnpj("12345678000199")
                .cep("12345-678")
                .numero("123")
                .logradouro("Rua Exemplo")
                .cidade("São Paulo")
                .estado("SP")
                .bairro("Centro")
                .complemento("Próximo ao Parque")
                .build();

        Usuario usuarioMock = new Usuario();
        Empresa empresaMock = new Empresa();
        when(usuarioService.cadastrarUsuario(any(Usuario.class))).thenReturn(usuarioMock);
        when(empresaService.salvarEmpresa(any(Empresa.class))).thenReturn(null); // Simula conflito

        ResponseEntity<CadastroRetornoDto> response = usuarioController.cadastrar(cadastroDto);

        assertEquals(409, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("Dado que o usuário não existe, quando o usuário é cadastrado, então o usuário é autenticado com sucesso.")
    void testAutenticarUsuario_Success() {
        UsuarioTokenDto tokenMock = new UsuarioTokenDto();
        when(usuarioService.autenticarUsuario(any())).thenReturn(tokenMock);

        ResponseEntity<UsuarioTokenDto> response = usuarioController.autenticar(any());

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

}
