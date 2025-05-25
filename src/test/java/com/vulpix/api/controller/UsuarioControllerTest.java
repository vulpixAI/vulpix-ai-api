package com.vulpix.api.controller;

import com.vulpix.api.controller.impl.UsuarioControllerImpl;
import com.vulpix.api.dto.autenticacao.LoginResponse;
import com.vulpix.api.dto.cadastroinicial.CadastroRequisicaoDto;
import com.vulpix.api.dto.cadastroinicial.CadastroRetornoDto;
import com.vulpix.api.dto.usuario.UsuarioLoginDto;
import com.vulpix.api.service.EmpresaService;
import com.vulpix.api.service.usuario.UsuarioService;
import com.vulpix.api.dto.autenticacao.UsuarioTokenDto;
import com.vulpix.api.entity.Usuario;
import com.vulpix.api.entity.Empresa;
import com.vulpix.api.utils.enums.StatusUsuario;
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
    private UsuarioControllerImpl usuarioController;

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
    void testCadastrarUsuario_Success() {
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
        usuarioMock.setStatus(StatusUsuario.AGUARDANDO_PAGAMENTO);
        Empresa empresaMock = new Empresa();

        when(usuarioService.cadastrarUsuario(any(Usuario.class), eq(cadastroDto.getCnpj()))).thenReturn(usuarioMock);
        when(empresaService.salvarEmpresa(any(Empresa.class))).thenReturn(empresaMock);

        ResponseEntity<CadastroRetornoDto> response = usuarioController.cadastrar(cadastroDto);

        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Quando o CNPJ já estiver cadastrado, deve lançar ConflitoException.")
    void testCadastrarUsuario_ConflictException() {
        CadastroRequisicaoDto cadastroDto = CadastroRequisicaoDto.builder()
                .cnpj("12345678000199")
                .build();

        when(usuarioService.cadastrarUsuario(any(Usuario.class), eq(cadastroDto.getCnpj())))
                .thenThrow(new com.vulpix.api.exception.exceptions.ConflitoException("Esse CNPJ já foi cadastrado."));

        assertThrows(com.vulpix.api.exception.exceptions.ConflitoException.class, () -> {
            usuarioController.cadastrar(cadastroDto);
        });
    }

    @Test
    @DisplayName("Dado um usuário válido, quando autenticar, então retorna token com status 200.")
    void testAutenticarUsuario_Success() {
        UsuarioLoginDto loginDto = new UsuarioLoginDto();
        loginDto.setEmail("joao.silva@empresa.com");
        loginDto.setSenha("SenhaSegura123!");
        loginDto.setDispositivoCode("codigoDispositivo");

        LoginResponse tokenMock = mock(LoginResponse.class);
        when(usuarioService.autenticarUsuario(any(UsuarioLoginDto.class))).thenReturn(tokenMock);

        ResponseEntity<LoginResponse> response = usuarioController.autenticar(loginDto);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

}
