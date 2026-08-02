package br.com.psicologia.marcia.service.agenda;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.psicologia.marcia.DTO.agenda.AgendamentoCalendarioDiaResponse;
import br.com.psicologia.marcia.DTO.agenda.AgendamentoConsultaRequest;
import br.com.psicologia.marcia.DTO.agenda.AgendamentoDataRequest;
import br.com.psicologia.marcia.DTO.agenda.AgendamentoIdRequest;
import br.com.psicologia.marcia.DTO.agenda.AgendamentoLembreteResponse;
import br.com.psicologia.marcia.DTO.agenda.AgendamentoMesRequest;
import br.com.psicologia.marcia.DTO.agenda.AgendamentoOrcamentoRequest;
import br.com.psicologia.marcia.DTO.agenda.AgendamentoPeriodoRequest;
import br.com.psicologia.marcia.DTO.agenda.AgendamentoResponse;
import br.com.psicologia.marcia.DTO.agenda.ResultadoAgendamentoConsultaResponse;
import br.com.psicologia.marcia.model.Agendamento;
import br.com.psicologia.marcia.model.Paciente;
import br.com.psicologia.marcia.model.enums.AcaoConflito;
import br.com.psicologia.marcia.model.enums.TipoAgendamento;
import br.com.psicologia.marcia.model.enums.TipoRecorrencia;
import br.com.psicologia.marcia.repository.agenda.AgendamentoRepository;
import br.com.psicologia.marcia.repository.paciente.ReadPacienteRepository;

@Service
public class AgendamentoService {

    private final AgendamentoRepository
        agendamentoRepository;

    private final ReadPacienteRepository
        pacienteRepository;

    public AgendamentoService(
        AgendamentoRepository agendamentoRepository,
        ReadPacienteRepository pacienteRepository
    ) {
        this.agendamentoRepository =
            agendamentoRepository;

        this.pacienteRepository =
            pacienteRepository;
    }

    @Transactional(readOnly = true)
    public List<AgendamentoResponse> listarTodos() {
        return agendamentoRepository
            .findAllByOrderByDataAgendamentoAscHorarioAsc()
            .stream()
            .map(this::converterParaResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<AgendamentoResponse> listarPorData(
        AgendamentoDataRequest request
    ) {
        if (request == null) {
            throw new IllegalArgumentException(
                "Os dados da busca são obrigatórios."
            );
        }

        if (request.getDataAgendamento() == null) {
            throw new IllegalArgumentException(
                "A data do agendamento é obrigatória."
            );
        }

        return agendamentoRepository
            .findByDataAgendamentoOrderByHorarioAsc(
                request.getDataAgendamento()
            )
            .stream()
            .map(this::converterParaResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<AgendamentoResponse> listarPorPeriodo(
        AgendamentoPeriodoRequest request
    ) {
        validarPeriodo(request);

        return agendamentoRepository
            .findByDataAgendamentoBetweenOrderByDataAgendamentoAscHorarioAsc(
                request.getDataInicial(),
                request.getDataFinal()
            )
            .stream()
            .map(this::converterParaResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<AgendamentoCalendarioDiaResponse>
        listarCalendarioMensal(
            AgendamentoMesRequest request
        ) {
        validarMes(request);

        YearMonth anoMes = YearMonth.of(
            request.getAno(),
            request.getMes()
        );

        LocalDate dataInicial =
            anoMes.atDay(1);

        LocalDate dataFinal =
            anoMes.atEndOfMonth();

        List<Agendamento> agendamentos =
            agendamentoRepository
                .findByDataAgendamentoBetweenOrderByDataAgendamentoAscHorarioAsc(
                    dataInicial,
                    dataFinal
                );

        Map<LocalDate, List<Agendamento>>
            agendamentosPorData =
                agendamentos
                    .stream()
                    .collect(
                        Collectors.groupingBy(
                            Agendamento
                                ::getDataAgendamento,
                            TreeMap::new,
                            Collectors.toList()
                        )
                    );

        return agendamentosPorData
            .entrySet()
            .stream()
            .map(entry -> {
                LocalDate data =
                    entry.getKey();

                List<Agendamento>
                    agendamentosDoDia =
                        entry.getValue();

                int quantidadeConsultas =
                    (int) agendamentosDoDia
                        .stream()
                        .filter(
                            agendamento ->
                                agendamento
                                    .getTipoAgendamento()
                                    == TipoAgendamento
                                        .CONSULTA
                        )
                        .count();

                int quantidadeOrcamentos =
                    (int) agendamentosDoDia
                        .stream()
                        .filter(
                            agendamento ->
                                agendamento
                                    .getTipoAgendamento()
                                    == TipoAgendamento
                                        .ORCAMENTO
                        )
                        .count();

                AgendamentoCalendarioDiaResponse
                    response =
                        new AgendamentoCalendarioDiaResponse();

                response.setDataAgendamento(
                    data
                );

                response.setQuantidadeTotal(
                    agendamentosDoDia.size()
                );

                response.setQuantidadeConsultas(
                    quantidadeConsultas
                );

                response.setQuantidadeOrcamentos(
                    quantidadeOrcamentos
                );

                return response;
            })
            .toList();
    }

    @Transactional(readOnly = true)
    public List<AgendamentoResponse> buscarConflitos(
        AgendamentoOrcamentoRequest request
    ) {
        validarCamposObrigatoriosOrcamento(
            request
        );

        return buscarConflitosPorDataEHorario(
            request.getDataAgendamento(),
            request.getHorario()
        );
    }

    @Transactional(readOnly = true)
    public List<AgendamentoResponse> buscarConflitos(
        AgendamentoConsultaRequest request
    ) {
        validarCamposObrigatoriosConsulta(
            request
        );

        List<LocalDate> datasRecorrencia =
            gerarDatasDaRecorrencia(request);

        List<AgendamentoResponse> conflitos =
            new ArrayList<>();

        for (LocalDate data : datasRecorrencia) {
            conflitos.addAll(
                buscarConflitosPorDataEHorario(
                    data,
                    request.getHorario()
                )
            );
        }

        return conflitos;
    }

    @Transactional
    public AgendamentoResponse salvarOrcamento(
        AgendamentoOrcamentoRequest request
    ) {
        validarCamposObrigatoriosOrcamento(
            request
        );

        Agendamento agendamento =
            new Agendamento();

        agendamento.setPaciente(null);

        agendamento.setNomePessoa(
            request.getNomePessoa().trim()
        );

        agendamento.setDataAgendamento(
            request.getDataAgendamento()
        );

        agendamento.setHorario(
            request.getHorario()
        );

        agendamento.setTipoAgendamento(
            TipoAgendamento.ORCAMENTO
        );

        agendamento.setTipoRecorrencia(
            TipoRecorrencia.SEM_RECORRENCIA
        );

        agendamento.setQuantidadeRecorrencia(1);
        agendamento.setGrupoRecorrencia(null);

        Agendamento agendamentoSalvo =
            agendamentoRepository.save(
                agendamento
            );

        return converterParaResponse(
            agendamentoSalvo
        );
    }

    @Transactional
    public ResultadoAgendamentoConsultaResponse
        salvarConsulta(
            AgendamentoConsultaRequest request
        ) {
        validarCamposObrigatoriosConsulta(
            request
        );

        Paciente paciente =
            pacienteRepository
                .findById(
                    request.getPacienteId()
                )
                .orElseThrow(
                    () ->
                        new IllegalArgumentException(
                            "Paciente não encontrado."
                        )
                );

        TipoRecorrencia tipoRecorrencia =
            normalizarTipoRecorrencia(
                request.getTipoRecorrencia()
            );

        int quantidadeSolicitada =
            definirQuantidadeRecorrencia(
                tipoRecorrencia,
                request.getQuantidadeRecorrencia()
            );

        AcaoConflito acaoConflito =
            normalizarAcaoConflito(
                request.getAcaoConflito()
            );

        List<LocalDate> datasSolicitadas =
            gerarDatasDaRecorrencia(
                request.getDataAgendamento(),
                tipoRecorrencia,
                quantidadeSolicitada
            );

        List<LocalDate> datasDisponiveis =
            new ArrayList<>();

        List<LocalDate> datasConflitantes =
            new ArrayList<>();

        List<AgendamentoResponse>
            agendamentosConflitantes =
                new ArrayList<>();

        for (LocalDate data : datasSolicitadas) {
            List<AgendamentoResponse>
                conflitosDaData =
                    buscarConflitosPorDataEHorario(
                        data,
                        request.getHorario()
                    );

            if (conflitosDaData.isEmpty()) {
                datasDisponiveis.add(data);
            } else {
                datasConflitantes.add(data);

                agendamentosConflitantes.addAll(
                    conflitosDaData
                );
            }
        }

        ResultadoAgendamentoConsultaResponse
            resultado =
                new ResultadoAgendamentoConsultaResponse();

        resultado.setAcaoConflito(
            acaoConflito
        );

        resultado.setQuantidadeSolicitada(
            quantidadeSolicitada
        );

        resultado.setDatasConflitantes(
            datasConflitantes
        );

        resultado.setAgendamentosConflitantes(
            agendamentosConflitantes
        );

        if (
            !datasConflitantes.isEmpty()
                && acaoConflito
                    == AcaoConflito.BLOQUEAR
        ) {
            resultado.setBloqueadoPorConflito(
                true
            );

            resultado.setQuantidadeSalva(0);

            resultado.setQuantidadeIgnorada(
                datasConflitantes.size()
            );

            resultado.setAgendamentosSalvos(
                List.of()
            );

            resultado.setMensagem(
                "A recorrência não foi salva porque existem datas conflitantes."
            );

            return resultado;
        }

        List<LocalDate> datasParaSalvar;

        if (
            acaoConflito
                == AcaoConflito
                    .SALVAR_APENAS_DISPONIVEIS
        ) {
            datasParaSalvar =
                datasDisponiveis;
        } else {
            datasParaSalvar =
                datasSolicitadas;
        }

        int quantidadeParaSalvar =
            datasParaSalvar.size();

        String grupoRecorrencia =
            definirGrupoRecorrencia(
                tipoRecorrencia,
                quantidadeParaSalvar
            );

        List<Agendamento> agendamentos =
            new ArrayList<>();

        for (LocalDate data : datasParaSalvar) {
            Agendamento agendamento =
                new Agendamento();

            agendamento.setPaciente(
                paciente
            );

            agendamento.setNomePessoa(null);

            agendamento.setDataAgendamento(
                data
            );

            agendamento.setHorario(
                request.getHorario()
            );

            agendamento.setTipoAgendamento(
                TipoAgendamento.CONSULTA
            );

            agendamento.setTipoRecorrencia(
                tipoRecorrencia
            );

            agendamento
                .setQuantidadeRecorrencia(
                    quantidadeParaSalvar
                );

            agendamento.setGrupoRecorrencia(
                grupoRecorrencia
            );

            agendamentos.add(agendamento);
        }

        List<AgendamentoResponse>
            agendamentosSalvos;

        if (agendamentos.isEmpty()) {
            agendamentosSalvos =
                List.of();
        } else {
            agendamentosSalvos =
                agendamentoRepository
                    .saveAll(agendamentos)
                    .stream()
                    .map(
                        this::converterParaResponse
                    )
                    .toList();
        }

        resultado.setBloqueadoPorConflito(
            false
        );

        resultado.setQuantidadeSalva(
            agendamentosSalvos.size()
        );

        resultado.setAgendamentosSalvos(
            agendamentosSalvos
        );

        if (
            acaoConflito
                == AcaoConflito
                    .SALVAR_APENAS_DISPONIVEIS
        ) {
            resultado.setQuantidadeIgnorada(
                datasConflitantes.size()
            );
        } else {
            resultado.setQuantidadeIgnorada(0);
        }

        resultado.setMensagem(
            definirMensagemResultado(
                acaoConflito,
                quantidadeSolicitada,
                agendamentosSalvos.size(),
                datasConflitantes.size()
            )
        );

        return resultado;
    }

    @Transactional(readOnly = true)
    public AgendamentoResponse buscarPorId(
        AgendamentoIdRequest request
    ) {
        if (request == null) {
            throw new IllegalArgumentException(
                "Os dados da busca são obrigatórios."
            );
        }

        if (request.getAgendamentoId() == null) {
            throw new IllegalArgumentException(
                "O ID do agendamento é obrigatório."
            );
        }

        if (request.getAgendamentoId() <= 0) {
            throw new IllegalArgumentException(
                "O ID do agendamento é inválido."
            );
        }

        Agendamento agendamento =
            agendamentoRepository
                .findById(
                    request.getAgendamentoId()
                )
                .orElseThrow(
                    () ->
                        new NoSuchElementException(
                            "Agendamento não encontrado."
                        )
                );

        return converterParaResponse(
            agendamento
        );
    }

    private void validarMes(
        AgendamentoMesRequest request
    ) {
        if (request == null) {
            throw new IllegalArgumentException(
                "Os dados do calendário são obrigatórios."
            );
        }

        if (request.getAno() == null) {
            throw new IllegalArgumentException(
                "O ano é obrigatório."
            );
        }

        if (request.getAno() < 1) {
            throw new IllegalArgumentException(
                "O ano informado é inválido."
            );
        }

        if (request.getMes() == null) {
            throw new IllegalArgumentException(
                "O mês é obrigatório."
            );
        }

        if (
            request.getMes() < 1
                || request.getMes() > 12
        ) {
            throw new IllegalArgumentException(
                "O mês deve estar entre 1 e 12."
            );
        }
    }

    private void validarPeriodo(
        AgendamentoPeriodoRequest request
    ) {
        if (request == null) {
            throw new IllegalArgumentException(
                "Os dados da busca são obrigatórios."
            );
        }

        if (request.getDataInicial() == null) {
            throw new IllegalArgumentException(
                "A data inicial é obrigatória."
            );
        }

        if (request.getDataFinal() == null) {
            throw new IllegalArgumentException(
                "A data final é obrigatória."
            );
        }

        if (
            request
                .getDataInicial()
                .isAfter(
                    request.getDataFinal()
                )
        ) {
            throw new IllegalArgumentException(
                "A data inicial não pode ser posterior à data final."
            );
        }
    }

    private List<AgendamentoResponse>
        buscarConflitosPorDataEHorario(
            LocalDate dataAgendamento,
            LocalTime horario
        ) {
        LocalTime inicioDaHora =
            LocalTime.of(
                horario.getHour(),
                0
            );

        LocalTime fimDaHora =
            LocalTime.of(
                horario.getHour(),
                59,
                59,
                999_999_999
            );

        return agendamentoRepository
            .findByDataAgendamentoAndHorarioBetweenOrderByHorarioAsc(
                dataAgendamento,
                inicioDaHora,
                fimDaHora
            )
            .stream()
            .map(this::converterParaResponse)
            .toList();
    }

    private List<LocalDate> gerarDatasDaRecorrencia(
        AgendamentoConsultaRequest request
    ) {
        TipoRecorrencia tipoRecorrencia =
            normalizarTipoRecorrencia(
                request.getTipoRecorrencia()
            );

        int quantidadeRecorrencia =
            definirQuantidadeRecorrencia(
                tipoRecorrencia,
                request.getQuantidadeRecorrencia()
            );

        return gerarDatasDaRecorrencia(
            request.getDataAgendamento(),
            tipoRecorrencia,
            quantidadeRecorrencia
        );
    }

    private List<LocalDate> gerarDatasDaRecorrencia(
        LocalDate dataInicial,
        TipoRecorrencia tipoRecorrencia,
        int quantidadeRecorrencia
    ) {
        List<LocalDate> datas =
            new ArrayList<>();

        for (
            int indice = 0;
            indice < quantidadeRecorrencia;
            indice++
        ) {
            LocalDate dataCalculada;

            switch (tipoRecorrencia) {
                case SEMANAL:
                    dataCalculada =
                        dataInicial.plusWeeks(
                            indice
                        );
                    break;

                case QUINZENAL:
                    dataCalculada =
                        dataInicial.plusWeeks(
                            indice * 2L
                        );
                    break;

                case MENSAL:
                    dataCalculada =
                        dataInicial.plusMonths(
                            indice
                        );
                    break;

                case SEM_RECORRENCIA:
                default:
                    dataCalculada =
                        dataInicial;
                    break;
            }

            datas.add(dataCalculada);
        }

        return datas;
    }
    
    @Transactional
    public void excluirAgendamento(
        AgendamentoIdRequest request
    ) {
        if (request == null) {
            throw new IllegalArgumentException(
                "Os dados da exclusão são obrigatórios."
            );
        }

        if (request.getAgendamentoId() == null) {
            throw new IllegalArgumentException(
                "O ID do agendamento é obrigatório."
            );
        }

        if (request.getAgendamentoId() <= 0) {
            throw new IllegalArgumentException(
                "O ID do agendamento é inválido."
            );
        }

        Agendamento agendamento =
            agendamentoRepository
                .findById(request.getAgendamentoId())
                .orElseThrow(
                    () -> new NoSuchElementException(
                        "Agendamento não encontrado."
                    )
                );

        agendamentoRepository.delete(agendamento);
    }
    
    @Transactional(readOnly = true)
    public AgendamentoLembreteResponse
        contarAgendamentosDoProximoDia() {

        LocalDate dataReferencia =
            LocalDate.now(
                ZoneId.of("America/Sao_Paulo")
            ).plusDays(1);

        long quantidade =
            agendamentoRepository
                .countByDataAgendamento(
                    dataReferencia
                );

        return new AgendamentoLembreteResponse(
            dataReferencia,
            quantidade
        );
    }
    
    @Transactional
    public long excluirAgendamentosAnterioresA(
        LocalDate dataLimite
    ) {
        if (dataLimite == null) {
            throw new IllegalArgumentException(
                "A data limite da limpeza é obrigatória."
            );
        }

        return agendamentoRepository
            .deleteByDataAgendamentoBefore(
                dataLimite
            );
    }
    
    @Transactional(readOnly = true)
    public List<AgendamentoCalendarioDiaResponse>
        listarCalendarioMesAtual() {

        YearMonth mesAtual =
            YearMonth.now(
                ZoneId.of("America/Sao_Paulo")
            );

        AgendamentoMesRequest request =
            new AgendamentoMesRequest();

        request.setAno(
            mesAtual.getYear()
        );

        request.setMes(
            mesAtual.getMonthValue()
        );

        return listarCalendarioMensal(request);
    }

    private TipoRecorrencia
        normalizarTipoRecorrencia(
            TipoRecorrencia tipoRecorrencia
        ) {
        if (tipoRecorrencia == null) {
            return TipoRecorrencia
                .SEM_RECORRENCIA;
        }

        return tipoRecorrencia;
    }

    private AcaoConflito normalizarAcaoConflito(
        AcaoConflito acaoConflito
    ) {
        if (acaoConflito == null) {
            return AcaoConflito.BLOQUEAR;
        }

        return acaoConflito;
    }

    private int definirQuantidadeRecorrencia(
        TipoRecorrencia tipoRecorrencia,
        Integer quantidadeSolicitada
    ) {
        if (
            tipoRecorrencia
                == TipoRecorrencia
                    .SEM_RECORRENCIA
        ) {
            return 1;
        }

        if (
            quantidadeSolicitada == null
                || quantidadeSolicitada < 2
        ) {
            throw new IllegalArgumentException(
                "A quantidade da recorrência deve ser no mínimo 2."
            );
        }

        return quantidadeSolicitada;
    }

    private String definirGrupoRecorrencia(
        TipoRecorrencia tipoRecorrencia,
        int quantidadeRecorrencia
    ) {
        if (
            tipoRecorrencia
                == TipoRecorrencia
                    .SEM_RECORRENCIA
                || quantidadeRecorrencia <= 1
        ) {
            return null;
        }

        return UUID.randomUUID().toString();
    }

    private String definirMensagemResultado(
        AcaoConflito acaoConflito,
        int quantidadeSolicitada,
        int quantidadeSalva,
        int quantidadeConflitante
    ) {
        if (
            acaoConflito
                == AcaoConflito
                    .SALVAR_APENAS_DISPONIVEIS
                && quantidadeSalva == 0
        ) {
            return "Nenhuma consulta foi salva porque todas as datas possuem conflito.";
        }

        if (
            acaoConflito
                == AcaoConflito
                    .SALVAR_APENAS_DISPONIVEIS
                && quantidadeConflitante > 0
        ) {
            return quantidadeSalva
                + " consulta(s) agendada(s) e "
                + quantidadeConflitante
                + " data(s) conflitante(s) ignorada(s).";
        }

        if (
            acaoConflito
                == AcaoConflito.SALVAR_TODOS
                && quantidadeConflitante > 0
        ) {
            return quantidadeSolicitada
                + " consulta(s) agendada(s), incluindo as datas conflitantes.";
        }

        return quantidadeSalva
            + " consulta(s) agendada(s) com sucesso.";
    }

    private void validarCamposObrigatoriosOrcamento(
        AgendamentoOrcamentoRequest request
    ) {
        if (request == null) {
            throw new IllegalArgumentException(
                "Os dados do agendamento são obrigatórios."
            );
        }

        if (
            request.getNomePessoa() == null
                || request
                    .getNomePessoa()
                    .isBlank()
        ) {
            throw new IllegalArgumentException(
                "O nome da pessoa é obrigatório."
            );
        }

        if (
            request.getDataAgendamento()
                == null
        ) {
            throw new IllegalArgumentException(
                "A data do agendamento é obrigatória."
            );
        }

        if (request.getHorario() == null) {
            throw new IllegalArgumentException(
                "O horário do agendamento é obrigatório."
            );
        }
    }

    private void validarCamposObrigatoriosConsulta(
        AgendamentoConsultaRequest request
    ) {
        if (request == null) {
            throw new IllegalArgumentException(
                "Os dados da consulta são obrigatórios."
            );
        }

        if (request.getPacienteId() == null) {
            throw new IllegalArgumentException(
                "O paciente é obrigatório."
            );
        }

        if (
            request.getDataAgendamento()
                == null
        ) {
            throw new IllegalArgumentException(
                "A data da consulta é obrigatória."
            );
        }

        if (request.getHorario() == null) {
            throw new IllegalArgumentException(
                "O horário da consulta é obrigatório."
            );
        }

        TipoRecorrencia tipoRecorrencia =
            normalizarTipoRecorrencia(
                request.getTipoRecorrencia()
            );

        definirQuantidadeRecorrencia(
            tipoRecorrencia,
            request.getQuantidadeRecorrencia()
        );
    }

    private AgendamentoResponse converterParaResponse(
        Agendamento agendamento
    ) {
        AgendamentoResponse response =
            new AgendamentoResponse();

        response.setId(
            agendamento.getId()
        );

        if (agendamento.getPaciente() != null) {
            response.setPacienteId(
                agendamento
                    .getPaciente()
                    .getId()
            );

            response.setPacientePessoa(
                agendamento
                    .getPaciente()
                    .getNomeCompleto()
            );
        } else {
            response.setPacienteId(null);

            response.setPacientePessoa(
                agendamento.getNomePessoa()
            );
        }

        response.setDataAgendamento(
            agendamento.getDataAgendamento()
        );

        response.setHorario(
            agendamento.getHorario()
        );

        response.setTipoAgendamento(
            agendamento.getTipoAgendamento()
        );

        response.setTipoRecorrencia(
            agendamento.getTipoRecorrencia()
        );

        response.setQuantidadeRecorrencia(
            agendamento
                .getQuantidadeRecorrencia()
        );

        response.setGrupoRecorrencia(
            agendamento.getGrupoRecorrencia()
        );

        response.setDataCriacao(
            agendamento.getDataCriacao()
        );

        return response;
    }
}