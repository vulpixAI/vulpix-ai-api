//package com.vulpix.api.controller;
//
//import com.vulpix.api.controller.impl.PublicacaoControllerImpl;
//import com.vulpix.api.dto.agent.PublicacaoGeradaRetorno;
//import com.vulpix.api.dto.publicacao.PostPublicacaoDto;
//import com.vulpix.api.dto.publicacao.PostPublicacaoResponse;
//import com.vulpix.api.entity.Empresa;
//import com.vulpix.api.entity.Publicacao;
//import com.vulpix.api.repository.PublicacaoRepository;
//import com.vulpix.api.service.usuario.autenticacao.UsuarioAutenticadoUtil;
//import com.vulpix.api.utils.helpers.EmpresaHelper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.*;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.userdetails.UserDetails;
//
//import java.time.OffsetDateTime;
//import java.util.UUID;
//
//import static org.mockito.Mockito.*;
//import static org.junit.jupiter.api.Assertions.*;
//
//@DisplayName("Teste da Clase PublicacaoController")
//class PublicacaoControllerTest {
//
//    @InjectMocks
//    private PublicacaoControllerImpl publicacaoController;
//
//
//    @Mock
//    private PublicacaoRepository publicacaoRepository;
//
//    @Mock
//    private UsuarioAutenticadoUtil usuarioAutenticadoUtil;
//
//    @Mock
//    private EmpresaHelper empresaHelper;
//
//    private Empresa empresa;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        empresa = new Empresa();
//        empresa.setId(UUID.randomUUID());
//        empresa.setRazaoSocial("Empresa Teste");
//    }
//
//    @Test
//    @DisplayName("Dado que o usuário está autenticado e a empresa foi encontrada, quando um post é criado, então o post é salvo com sucesso.")
//    void testCriarPostEmpresaNaoEncontrada() {
//        PostPublicacaoDto postDto = new PostPublicacaoDto();
//        postDto.setCaption("Novo Post");
//        postDto.setImageUrl("http://exemplo.com/imagem.jpg");
//        postDto.setAgendamento(OffsetDateTime.now().plusHours(1));
//
//        when(usuarioAutenticadoUtil.getUsuarioDetalhes()).thenReturn(mockUserDetails());
//        when(empresaHelper.buscarEmpresaPeloUsuario(anyString())).thenReturn(null);
//
//        ResponseEntity<PostPublicacaoResponse> response = publicacaoController.criarPost(postDto);
//
//        assertEquals(404, response.getStatusCodeValue());
//        verify(publicacaoRepository, times(0)).save(any(Publicacao.class));
//    }
//
//    private UserDetails mockUserDetails() {
//        return mock(UserDetails.class);
//    }
//
//    private PublicacaoGeradaRetorno mockPublicacaoGeradaRetorno() {
//        PublicacaoGeradaRetorno retorno = new PublicacaoGeradaRetorno();
//        retorno.setImagem1("http://exemplo.com/imagem.jpg");
//        return retorno;
//    }
//}
