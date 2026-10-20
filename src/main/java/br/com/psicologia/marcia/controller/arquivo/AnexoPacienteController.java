
package br.com.psicologia.marcia.controller.arquivo;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.CacheControl;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.multipart.MultipartFile;

import br.com.psicologia.marcia.service.arquivo.AnexoPacienteService;
import br.com.psicologia.marcia.service.arquivo.AnexoPacienteService.AnexoListado;
import br.com.psicologia.marcia.service.arquivo.AnexoPacienteService.AnexoRecuperado;
import br.com.psicologia.marcia.service.arquivo.AnexoPacienteService.AnexoSalvo;

@RestController
@RequestMapping("/api/arquivo")
public class AnexoPacienteController {

    private final AnexoPacienteService anexoPacienteService;

    public AnexoPacienteController(
            AnexoPacienteService anexoPacienteService
    ) {
        this.anexoPacienteService = anexoPacienteService;
    }

    /*
     * ANEXAR DOCUMENTO
     *
     * Endpoint:
     * POST /api/arquivo/anexar
     *
     * Content-Type:
     * multipart/form-data
     *
     * Campos:
     * pacienteId -> Long
     * pasta      -> String
     * arquivo    -> MultipartFile
     *
     * A autenticacao JWT e obrigatoria,
     * conforme SecurityConfigurations.
     *
     * Todos os perfis autenticados
     * possuem permissao para anexar.
     *
     * O ID do paciente e enviado no
     * corpo da requisicao, nao na URL.
     */
    @PostMapping(
            value = "/anexar",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> anexar(
            @RequestParam("pacienteId") Long pacienteId,
            @RequestParam("pasta") String pasta,
            @RequestPart("arquivo") MultipartFile arquivo
    ) {

        try {

            AnexoSalvo anexoSalvo =
                    anexoPacienteService.anexar(
                            pacienteId,
                            pasta,
                            arquivo
                    );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(anexoSalvo);

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(
                            Collections.singletonMap(
                                    "mensagem",
                                    e.getMessage()
                            )
                    );

        } catch (NoSuchElementException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            Collections.singletonMap(
                                    "mensagem",
                                    e.getMessage()
                            )
                    );
        }
    }

    /*
     * TASK 2.6
     *
     * LISTAR ANEXOS DO PACIENTE
     *
     * Endpoint:
     * POST /api/arquivo/listar-anexos
     *
     * Content-Type:
     * application/json
     *
     * Retorna apenas metadados.
     * Os caminhos fisicos e nomes UUID
     * nao sao enviados ao frontend.
     */
    @PostMapping(
            value = "/listar-anexos",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> listarAnexos(
            @RequestBody ListarAnexosRequest request
    ) {

        try {

            Long pacienteId =
                    request == null
                            ? null
                            : request.pacienteId();

            List<AnexoListado> anexos =
                    anexoPacienteService.listarAnexos(
                            pacienteId
                    );

            return ResponseEntity
                    .ok(anexos);

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(
                            Collections.singletonMap(
                                    "mensagem",
                                    e.getMessage()
                            )
                    );

        } catch (NoSuchElementException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            Collections.singletonMap(
                                    "mensagem",
                                    e.getMessage()
                            )
                    );
        }
    }

    /*
     * TASK 2.6
     *
     * RECUPERAR ANEXO DO PACIENTE
     *
     * Endpoint:
     * POST /api/arquivo/recuperar-anexo
     *
     * Content-Type da requisicao:
     * application/json
     *
     * Corpo esperado:
     * {
     *     "pacienteId": 3,
     *     "anexoId": 1
     * }
     *
     * A Service valida o paciente,
     * o vinculo do documento e recupera
     * o arquivo do diretorio privado.
     */
    @PostMapping(
            value = "/recuperar-anexo",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> recuperarAnexo(
            @RequestBody RecuperarAnexoRequest request
    ) {

        try {

            Long pacienteId =
                    request == null
                            ? null
                            : request.pacienteId();

            Long anexoId =
                    request == null
                            ? null
                            : request.anexoId();

            AnexoRecuperado anexo =
                    anexoPacienteService.recuperarAnexo(
                            pacienteId,
                            anexoId
                    );

            MediaType tipoConteudo =
                    MediaType.parseMediaType(
                            anexo.tipoMime()
                    );

            /*
             * PDF:
             * disponibiliza para visualizacao.
             *
             * DOCX:
             * disponibiliza para download.
             *
             * O nome original e preservado
             * sem codificacao RFC 2047.
             */
            boolean visualizarPdf =
                    MediaType.APPLICATION_PDF_VALUE.equals(
                            anexo.tipoMime()
                    );

            String contentDisposition =
                    criarContentDisposition(
                            anexo.nomeOriginal(),
                            visualizarPdf
                    );

            return ResponseEntity
                    .ok()
                    .contentType(tipoConteudo)
                    .contentLength(
                            anexo.tamanhoBytes()
                    )
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            contentDisposition
                    )
                    .header(
                            "X-Content-Type-Options",
                            "nosniff"
                    )
                    .cacheControl(
                            CacheControl.noStore()
                    )
                    .body(
                            anexo.recurso()
                    );

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(
                            Collections.singletonMap(
                                    "mensagem",
                                    e.getMessage()
                            )
                    );

        } catch (NoSuchElementException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            Collections.singletonMap(
                                    "mensagem",
                                    e.getMessage()
                            )
                    );
        }
    }

    /*
     * CORRECAO DO NOME NO DOWNLOAD
     *
     * Gera um Content-Disposition
     * compativel com diferentes clientes.
     *
     * filename:
     * Nome ASCII para compatibilidade.
     *
     * filename*:
     * Nome original em UTF-8, utilizado
     * quando contem caracteres especiais.
     *
     * Evita que a codificacao RFC 2047
     * apareca literalmente no nome salvo
     * pelo Postman.
     */
    private String criarContentDisposition(
            String nomeOriginal,
            boolean visualizarPdf
    ) {

        /*
         * Converte caracteres acentuados
         * para uma alternativa ASCII.
         *
         * Exemplo:
         * Relatório_Psicológico.pdf
         * Relatorio_Psicologico.pdf
         */
        String nomeNormalizado =
                Normalizer.normalize(
                        nomeOriginal,
                        Normalizer.Form.NFD
                );

        String nomeAscii =
                nomeNormalizado
                        .replaceAll("\\p{M}", "")
                        .replaceAll(
                                "[^A-Za-z0-9._ -]",
                                "_"
                        );

        /*
         * O nome de compatibilidade
         * nunca deve ficar vazio.
         */
        if (nomeAscii.isBlank()) {

            nomeAscii = "anexo";
        }

        ContentDisposition disposicao;

        if (visualizarPdf) {

            disposicao =
                    ContentDisposition
                            .inline()
                            .filename(nomeAscii)
                            .build();

        } else {

            disposicao =
                    ContentDisposition
                            .attachment()
                            .filename(nomeAscii)
                            .build();
        }

        String cabecalho =
                disposicao.toString();

        /*
         * Se o nome original possui
         * caracteres fora do ASCII,
         * acrescenta filename* em UTF-8.
         *
         * Clientes modernos podem usar
         * o nome original acentuado.
         */
        if (!nomeOriginal.equals(nomeAscii)) {

            String nomeCodificado =
                    URLEncoder.encode(
                            nomeOriginal,
                            StandardCharsets.UTF_8
                    ).replace(
                            "+",
                            "%20"
                    );

            cabecalho +=
                    "; filename*=UTF-8''"
                            + nomeCodificado;
        }

        return cabecalho;
    }

    /*
     * DTO de entrada para listagem.
     */
    public record ListarAnexosRequest(
            Long pacienteId
    ) {
    }

    /*
     * DTO de entrada para recuperacao
     * de um anexo especifico.
     */
    public record RecuperarAnexoRequest(
            Long pacienteId,
            Long anexoId
    ) {
    }
}
