package br.com.psicologia.marcia.controller.declaracao;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.psicologia.marcia.DTO.declaracao.DeclaracaoComparecimentoRequest;
import br.com.psicologia.marcia.DTO.declaracao.DeclaracaoComparecimentoResponse;
import br.com.psicologia.marcia.DTO.declaracao.DeclaracaoComparecimentoWordArquivo;
import br.com.psicologia.marcia.DTO.declaracao.DeclaracaoComparecimentoWordRequest;
import br.com.psicologia.marcia.service.declaracao.DeclaracaoComparecimentoService;
import br.com.psicologia.marcia.service.declaracao.DeclaracaoComparecimentoWordService;

@RestController
@RequestMapping("/api/declaracoes/comparecimento")
public class DeclaracaoComparecimentoController {

    private final DeclaracaoComparecimentoService declaracaoComparecimentoService;
    private final DeclaracaoComparecimentoWordService declaracaoComparecimentoWordService;

    public DeclaracaoComparecimentoController(
            DeclaracaoComparecimentoService declaracaoComparecimentoService,
            DeclaracaoComparecimentoWordService declaracaoComparecimentoWordService
    ) {
        this.declaracaoComparecimentoService = declaracaoComparecimentoService;
        this.declaracaoComparecimentoWordService = declaracaoComparecimentoWordService;
    }

    @PostMapping("/salvar")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<DeclaracaoComparecimentoResponse> salvar(
            @RequestBody DeclaracaoComparecimentoRequest request
    ) {
        DeclaracaoComparecimentoResponse response = declaracaoComparecimentoService.salvar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/baixar-word")
    public ResponseEntity<byte[]> baixarWord(
            @RequestBody DeclaracaoComparecimentoWordRequest request
    ) {
        DeclaracaoComparecimentoWordArquivo arquivo = declaracaoComparecimentoWordService.baixarWord(request);

        String nomeArquivoCodificado = declaracaoComparecimentoWordService
                .codificarNomeArquivo(arquivo.getNomeArquivo());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                ))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + arquivo.getNomeArquivo() + "\"; filename*=UTF-8''" + nomeArquivoCodificado
                )
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
                .body(arquivo.getConteudo());
    }
}