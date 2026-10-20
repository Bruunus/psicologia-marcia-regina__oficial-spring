
package br.com.psicologia.marcia.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(
    name = "anexo_paciente",
    indexes = {
        @Index(
            name = "idx_anexo_paciente_id",
            columnList = "paciente_id"
        ),
        @Index(
            name = "idx_anexo_paciente_pasta",
            columnList = "paciente_id, pasta"
        )
    }
)
public class AnexoPaciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * Paciente ao qual o anexo pertence.
     *
     * Um paciente pode possuir varios anexos.
     * Cada anexo pertence a um unico paciente.
     *
     * Nao utiliza CascadeType.REMOVE.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "paciente_id",
        nullable = false,
        foreignKey = @ForeignKey(
            name = "fk_anexo_paciente"
        )
    )
    private Paciente paciente;

    /*
     * Pasta do historico em que o documento
     * sera apresentado.
     *
     * Exemplos:
     * QUEIXAS
     * RELATORIOS
     * FINANCEIRO
     * LAUDOS
     * OUTROS_ARQUIVOS
     *
     * Os valores permitidos serao validados
     * pela Service de anexos.
     */
    @Column(
        name = "pasta",
        nullable = false,
        length = 60
    )
    private String pasta;

    /*
     * Nome original enviado pelo usuario.
     *
     * Utilizado somente para exibicao.
     * Nunca deve ser utilizado diretamente
     * para construir caminhos no servidor.
     */
    @Column(
        name = "nome_original",
        nullable = false,
        length = 255
    )
    private String nomeOriginal;

    /*
     * Nome unico gerado pelo backend.
     *
     * A Service utilizara um identificador
     * aleatorio para evitar colisao entre
     * arquivos enviados.
     */
    @Column(
        name = "nome_armazenado",
        nullable = false,
        unique = true,
        length = 255
    )
    private String nomeArmazenado;

    /*
     * Formato validado pelo backend.
     *
     * Valores previstos:
     * PDF
     * DOCX
     */
    @Column(
        name = "formato",
        nullable = false,
        length = 10
    )
    private String formato;

    /*
     * Tipo de conteudo validado.
     *
     * Exemplos:
     * application/pdf
     *
     * application/vnd.openxmlformats-
     * officedocument.wordprocessingml.document
     */
    @Column(
        name = "tipo_mime",
        nullable = false,
        length = 150
    )
    private String tipoMime;

    /*
     * Tamanho do arquivo em bytes.
     */
    @Column(
        name = "tamanho_bytes",
        nullable = false
    )
    private Long tamanhoBytes;

    /*
     * Caminho relativo do arquivo dentro
     * do diretorio privado de armazenamento.
     *
     * O diretorio raiz sera definido em uma
     * configuracao separada.
     *
     * Nunca armazenar uma URL publica aqui.
     */
    @Column(
        name = "caminho_relativo",
        nullable = false,
        length = 512
    )
    private String caminhoRelativo;

    /*
     * Data e hora em que o anexo foi
     * registrado no sistema.
     */
    @Column(
        name = "data_anexo",
        nullable = false,
        updatable = false
    )
    private LocalDateTime dataAnexo;

    public AnexoPaciente() {
    }

    /*
     * Define automaticamente a data de
     * criacao caso ainda nao tenha sido
     * informada.
     */
    @PrePersist
    private void prepararPersistencia() {

        if (this.dataAnexo == null) {
            this.dataAnexo = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public String getPasta() {
        return pasta;
    }

    public void setPasta(String pasta) {
        this.pasta = pasta;
    }

    public String getNomeOriginal() {
        return nomeOriginal;
    }

    public void setNomeOriginal(String nomeOriginal) {
        this.nomeOriginal = nomeOriginal;
    }

    public String getNomeArmazenado() {
        return nomeArmazenado;
    }

    public void setNomeArmazenado(String nomeArmazenado) {
        this.nomeArmazenado = nomeArmazenado;
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    public String getTipoMime() {
        return tipoMime;
    }

    public void setTipoMime(String tipoMime) {
        this.tipoMime = tipoMime;
    }

    public Long getTamanhoBytes() {
        return tamanhoBytes;
    }

    public void setTamanhoBytes(Long tamanhoBytes) {
        this.tamanhoBytes = tamanhoBytes;
    }

    public String getCaminhoRelativo() {
        return caminhoRelativo;
    }

    public void setCaminhoRelativo(String caminhoRelativo) {
        this.caminhoRelativo = caminhoRelativo;
    }

    public LocalDateTime getDataAnexo() {
        return dataAnexo;
    }

    public void setDataAnexo(LocalDateTime dataAnexo) {
        this.dataAnexo = dataAnexo;
    }
}
