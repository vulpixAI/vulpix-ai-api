package com.vulpix.api.Service.Integracoes.Graph;

import com.vulpix.api.Entity.Empresa;
import com.vulpix.api.Entity.Integracao;
import com.vulpix.api.Repository.IntegracaoRepository;
import com.vulpix.api.Service.Usuario.Autenticacao.UsuarioAutenticadoUtil;
import com.vulpix.api.Utils.Enum.TipoIntegracao;
import com.vulpix.api.Utils.Helpers.EmpresaHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class InsightsScheduler {

    @Autowired
    private IntegracaoRepository integracaoRepository;

    @Autowired
    private InsightService insightService;

    @Autowired
    private UsuarioAutenticadoUtil usuarioAutenticadoUtil;
    @Autowired
    private EmpresaHelper empresaHelper;

    @Scheduled(cron = "0 0 * * * *")
    public void buscarInsights() {
        List<Integracao> integracoes = integracaoRepository.findByStatusAndTipo(true, TipoIntegracao.INSTAGRAM);

        for (Integracao integracao : integracoes) {
            try {
                insightService.fetchInsightsBatch(integracao);
                System.out.println("Insights atualizados para a integração: " + integracao.getId());
            } catch (Exception e) {
                System.err.println("Erro ao processar integração ID: " + integracao.getId());
                e.printStackTrace();
            }
        }
    }
}
