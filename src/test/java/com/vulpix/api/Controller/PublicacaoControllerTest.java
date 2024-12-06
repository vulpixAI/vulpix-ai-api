package com.vulpix.api.Controller;

import com.vulpix.api.Dto.Agent.PublicacaoGeradaRetorno;
import com.vulpix.api.Dto.Publicacao.PostPublicacaoDto;
import com.vulpix.api.Dto.Publicacao.PostPublicacaoResponse;
import com.vulpix.api.Entity.Empresa;
import com.vulpix.api.Entity.Publicacao;
import com.vulpix.api.Repository.PublicacaoRepository;
import com.vulpix.api.Service.Usuario.Autenticacao.UsuarioAutenticadoUtil;
import com.vulpix.api.Utils.Helpers.EmpresaHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Teste da Clase PublicacaoController")
class PublicacaoControllerTest {

    @InjectMocks
    private PublicacaoController publicacaoController;


    @Mock
    private PublicacaoRepository publicacaoRepository;

    @Mock
    private UsuarioAutenticadoUtil usuarioAutenticadoUtil;

    @Mock
    private EmpresaHelper empresaHelper;

    private Empresa empresa;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        empresa = new Empresa();
        empresa.setId(UUID.randomUUID());
        empresa.setRazaoSocial("Empresa Teste");
    }

    @Test
    @DisplayName("Dado que o usuário está autenticado e a empresa foi encontrada, quando um post é criado, então o post é salvo com sucesso.")
    void testCriarPostEmpresaNaoEncontrada() {
        PostPublicacaoDto postDto = new PostPublicacaoDto();
        postDto.setCaption("Novo Post");
        postDto.setImageUrl("http://exemplo.com/imagem.jpg");
        postDto.setAgendamento(OffsetDateTime.now().plusHours(1));

        when(usuarioAutenticadoUtil.getUsuarioDetalhes()).thenReturn(mockUserDetails());
        when(empresaHelper.buscarEmpresaPeloUsuario(anyString())).thenReturn(null);

        ResponseEntity<PostPublicacaoResponse> response = publicacaoController.criarPost(postDto);

        assertEquals(404, response.getStatusCodeValue());
        verify(publicacaoRepository, times(0)).save(any(Publicacao.class));
    }

    private UserDetails mockUserDetails() {
        return mock(UserDetails.class);
    }

    private PublicacaoGeradaRetorno mockPublicacaoGeradaRetorno() {
        PublicacaoGeradaRetorno retorno = new PublicacaoGeradaRetorno();
        retorno.setImagem1("http://exemplo.com/imagem.jpg");
        return retorno;
    }
}
