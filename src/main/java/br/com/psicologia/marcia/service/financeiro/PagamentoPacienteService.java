package br.com.psicologia.marcia.service.financeiro;

import br.com.psicologia.marcia.DTO.financeiro.BuscarPagamentoPacienteRequest;
import br.com.psicologia.marcia.DTO.financeiro.ConfirmarPagamentoRequest;
import br.com.psicologia.marcia.DTO.financeiro.DeletarPagamentoPacienteRequest;
import br.com.psicologia.marcia.DTO.financeiro.EditarPagamentoPacienteRequest;
import br.com.psicologia.marcia.DTO.financeiro.ListarPagamentoPacienteRequest;
import br.com.psicologia.marcia.DTO.financeiro.PagamentoPacienteResponse;
import br.com.psicologia.marcia.DTO.financeiro.SalvarPagamentoPacienteRequest;
import br.com.psicologia.marcia.model.Paciente;
import br.com.psicologia.marcia.model.PagamentoPaciente;
import br.com.psicologia.marcia.model.enums.StatusDelete;
import br.com.psicologia.marcia.model.enums.StatusPagamento;
import br.com.psicologia.marcia.model.enums.TipoDePagamento;
import br.com.psicologia.marcia.repository.financeiro.PagamentoPacienteRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
public class PagamentoPacienteService {

    private static final ZoneId ZONE_ID_SAO_PAULO = ZoneId.of("America/Sao_Paulo");

    private final PagamentoPacienteRepository pagamentoPacienteRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public PagamentoPacienteService(PagamentoPacienteRepository pagamentoPacienteRepository) {
        this.pagamentoPacienteRepository = pagamentoPacienteRepository;
    }

    @Transactional
    public PagamentoPacienteResponse salvar(SalvarPagamentoPacienteRequest request) {
        validarSalvar(request);

        Paciente paciente = buscarPacienteObrigatorio(request.getPacienteId());

        PagamentoPaciente pagamentoPaciente = new PagamentoPaciente();
        pagamentoPaciente.setPaciente(paciente);
        pagamentoPaciente.setQuantidadeSessao(request.getQuantidadeSessao());
        pagamentoPaciente.setValorSessao(request.getValorSessao());
        pagamentoPaciente.setDtDoAtendimento(request.getDtDoAtendimento());
        pagamentoPaciente.setDtPagPrevisto(request.getDtPagPrevisto());
        pagamentoPaciente.setTipoDePagamento(request.getTipoDePagamento());
        pagamentoPaciente.setStatusPagamento(StatusPagamento.EM_ABERTO);
        pagamentoPaciente.setStatusDelete(StatusDelete.NAO_DELETADO);

        PagamentoPaciente pagamentoSalvo = pagamentoPacienteRepository.save(pagamentoPaciente);

        return montarResponse(pagamentoSalvo);
    }

    public List<PagamentoPacienteResponse> listarPorPaciente(ListarPagamentoPacienteRequest request) {
        validarPacienteId(request.getPacienteId());

        return pagamentoPacienteRepository
                .findByPacienteIdAndStatusDeleteOrderByDtPagPrevistoDesc(
                        request.getPacienteId(),
                        StatusDelete.NAO_DELETADO
                )
                .stream()
                .map(this::montarResponse)
                .toList();
    }

    public PagamentoPacienteResponse buscarPorId(BuscarPagamentoPacienteRequest request) {
        validarPagamentoId(request.getPagamentoId());

        PagamentoPaciente pagamentoPaciente = buscarPagamentoObrigatorio(request.getPagamentoId());

        return montarResponse(pagamentoPaciente);
    }

    @Transactional
    public PagamentoPacienteResponse editar(EditarPagamentoPacienteRequest request) {
        validarEditar(request);

        PagamentoPaciente pagamentoPaciente = buscarPagamentoObrigatorio(request.getPagamentoId());

        pagamentoPaciente.setQuantidadeSessao(request.getQuantidadeSessao());
        pagamentoPaciente.setValorSessao(request.getValorSessao());
        pagamentoPaciente.setDtDoAtendimento(request.getDtDoAtendimento());
        pagamentoPaciente.setDtPagPrevisto(request.getDtPagPrevisto());
        pagamentoPaciente.setTipoDePagamento(request.getTipoDePagamento());

        PagamentoPaciente pagamentoAtualizado = pagamentoPacienteRepository.save(pagamentoPaciente);

        return montarResponse(pagamentoAtualizado);
    }

    @Transactional
    public PagamentoPacienteResponse confirmarPagamento(ConfirmarPagamentoRequest request) {
        validarPagamentoId(request.getPagamentoId());

        PagamentoPaciente pagamentoPaciente = buscarPagamentoObrigatorio(request.getPagamentoId());

        pagamentoPaciente.setStatusPagamento(StatusPagamento.PAGO);

        PagamentoPaciente pagamentoAtualizado = pagamentoPacienteRepository.save(pagamentoPaciente);

        return montarResponse(pagamentoAtualizado);
    }

    @Transactional
    public void deletar(DeletarPagamentoPacienteRequest request) {
        validarPagamentoId(request.getPagamentoId());

        PagamentoPaciente pagamentoPaciente = buscarPagamentoObrigatorio(request.getPagamentoId());

        pagamentoPaciente.setStatusDelete(StatusDelete.DELETADO);

        pagamentoPacienteRepository.save(pagamentoPaciente);
    }

    private Paciente buscarPacienteObrigatorio(Long pacienteId) {
        Paciente paciente = entityManager.find(Paciente.class, pacienteId);

        if (paciente == null) {
            throw new IllegalArgumentException("Paciente não encontrado.");
        }

        return paciente;
    }

    private PagamentoPaciente buscarPagamentoObrigatorio(Long pagamentoId) {
        return pagamentoPacienteRepository
                .findByIdAndStatusDelete(pagamentoId, StatusDelete.NAO_DELETADO)
                .orElseThrow(() -> new IllegalArgumentException("Pagamento não encontrado."));
    }

    private PagamentoPacienteResponse montarResponse(PagamentoPaciente pagamentoPaciente) {
        PagamentoPacienteResponse response = new PagamentoPacienteResponse();

        response.setId(pagamentoPaciente.getId());

        if (pagamentoPaciente.getPaciente() != null) {
            response.setPacienteId(pagamentoPaciente.getPaciente().getId());
        }

        response.setQuantidadeSessao(pagamentoPaciente.getQuantidadeSessao());
        response.setValorSessao(pagamentoPaciente.getValorSessao());
        response.setDtDoAtendimento(pagamentoPaciente.getDtDoAtendimento());
        response.setDtPagPrevisto(pagamentoPaciente.getDtPagPrevisto());

        response.setTipoDePagamento(pagamentoPaciente.getTipoDePagamento());
        response.setTipoDePagamentoDescricao(
                montarDescricaoTipoPagamento(pagamentoPaciente.getTipoDePagamento())
        );

        response.setStatusPagamento(pagamentoPaciente.getStatusPagamento());

        String statusDescricao = montarDescricaoStatus(pagamentoPaciente);
        String statusCor = montarCorStatus(pagamentoPaciente);

        response.setStatusDescricao(statusDescricao);
        response.setStatusCor(statusCor);

        return response;
    }

    private String montarDescricaoTipoPagamento(TipoDePagamento tipoDePagamento) {
        if (tipoDePagamento == null) {
            return "";
        }

        return switch (tipoDePagamento) {
            case AVULSO -> "Avulso";
            case MENSAL -> "Mensal";
        };
    }

    private String montarDescricaoStatus(PagamentoPaciente pagamentoPaciente) {
        if (StatusPagamento.PAGO.equals(pagamentoPaciente.getStatusPagamento())) {
            return "Pago";
        }

        LocalDate hoje = LocalDate.now(ZONE_ID_SAO_PAULO);
        LocalDate dataPagamentoPrevisto = pagamentoPaciente.getDtPagPrevisto();

        if (hoje.isBefore(dataPagamentoPrevisto)) {
            return "Á cobrar";
        }

        if (hoje.isEqual(dataPagamentoPrevisto)) {
            return "Hoje";
        }

        return "Pendente";
    }

    private String montarCorStatus(PagamentoPaciente pagamentoPaciente) {
        if (StatusPagamento.PAGO.equals(pagamentoPaciente.getStatusPagamento())) {
            return "verde";
        }

        LocalDate hoje = LocalDate.now(ZONE_ID_SAO_PAULO);
        LocalDate dataPagamentoPrevisto = pagamentoPaciente.getDtPagPrevisto();

        if (hoje.isBefore(dataPagamentoPrevisto)) {
            return "azul";
        }

        if (hoje.isEqual(dataPagamentoPrevisto)) {
            return "laranja";
        }

        return "vermelho";
    }

    private void validarSalvar(SalvarPagamentoPacienteRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Dados do pagamento não informados.");
        }

        validarPacienteId(request.getPacienteId());

        validarCamposPagamento(
                request.getQuantidadeSessao(),
                request.getValorSessao(),
                request.getDtDoAtendimento(),
                request.getDtPagPrevisto(),
                request.getTipoDePagamento()
        );
    }

    private void validarEditar(EditarPagamentoPacienteRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Dados do pagamento não informados.");
        }

        validarPagamentoId(request.getPagamentoId());

        validarCamposPagamento(
                request.getQuantidadeSessao(),
                request.getValorSessao(),
                request.getDtDoAtendimento(),
                request.getDtPagPrevisto(),
                request.getTipoDePagamento()
        );
    }

    private void validarPacienteId(Long pacienteId) {
        if (pacienteId == null) {
            throw new IllegalArgumentException("Paciente não informado.");
        }

        if (pacienteId <= 0) {
            throw new IllegalArgumentException("Paciente inválido.");
        }
    }

    private void validarPagamentoId(Long pagamentoId) {
        if (pagamentoId == null) {
            throw new IllegalArgumentException("Pagamento não informado.");
        }

        if (pagamentoId <= 0) {
            throw new IllegalArgumentException("Pagamento inválido.");
        }
    }

    private void validarCamposPagamento(
            Integer quantidadeSessao,
            BigDecimal valorSessao,
            LocalDate dtDoAtendimento,
            LocalDate dtPagPrevisto,
            TipoDePagamento tipoDePagamento
    ) {
        if (quantidadeSessao == null) {
            throw new IllegalArgumentException("Quantidade de sessão não informada.");
        }

        if (quantidadeSessao < 1) {
            throw new IllegalArgumentException("Quantidade de sessão deve ser no mínimo 1.");
        }

        if (valorSessao == null) {
            throw new IllegalArgumentException("Valor da sessão não informado.");
        }

        if (valorSessao.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor da sessão deve ser maior que zero.");
        }

        if (dtDoAtendimento == null) {
            throw new IllegalArgumentException("Data do atendimento não informada.");
        }

        if (dtPagPrevisto == null) {
            throw new IllegalArgumentException("Data prevista de pagamento não informada.");
        }

        if (tipoDePagamento == null) {
            throw new IllegalArgumentException("Tipo de pagamento não informado.");
        }
    }
}