package br.com.utfpr.porta.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import br.com.utfpr.porta.modelo.serializacao.LocalDateSerializador;

@Entity
@Table(name = "anuncio")
public class Anuncio implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long codigo;
	
	@ManyToOne
	@JoinColumn(name = "codigo_estabelecimento")
	private Estabelecimento estabelecimento;
	
	@Size(min = 5, max = 200, message = "Descrição deve ter entre 5 e 200 caracteres")
	private String descricao;
	
	@Size(min = 1, max = 50, message = "Descrição resumida deve ter até 50 caracteres")
	@Column(name = "descricao_resumida")
	private String descricaoResumida;
	
	@NotNull(message = "Informe um preço")
	@DecimalMin(value = "0.01", message = "O valor do anúncio deve ser maior que R$0,01")
	@DecimalMax(value = "9999999.99", message = "O valor do anúncio deve ser menor que R$9.999.999,99")
	private BigDecimal preco;
		
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	@JsonSerialize(using = LocalDateSerializador.class)
	@Column(name = "data_publicacao")
	private LocalDate dataPublicacao;
	
	@NotNull(message = "Informe uma data de expiração")
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	@JsonSerialize(using = LocalDateSerializador.class)
	@Column(name = "data_expiracao")
	private LocalDate dataExpiracao;
		
	@Column(name = "data_hora_criacao", updatable=false)
	private LocalDateTime dataHoraCriacao;
	
	@Column(name = "data_hora_alteracao")
	private LocalDateTime dataHoraAlteracao;
	
	@Transient
	private Long qtdeInteressados;
	
	public Anuncio() {
		this.dataPublicacao = LocalDate.now();
	}
	
	@PrePersist
	private void prePersist() {
		this.dataHoraCriacao = LocalDateTime.now();
		this.dataHoraAlteracao = LocalDateTime.now();
	}
	
	@PreUpdate
	private void preUpdate() {
		this.dataHoraAlteracao = LocalDateTime.now();
	}
	
	public boolean isNovo() {
		return codigo == null;
	}

	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public Estabelecimento getEstabelecimento() {
		return estabelecimento;
	}

	public void setEstabelecimento(Estabelecimento estabelecimento) {
		this.estabelecimento = estabelecimento;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public String getDescricaoResumida() {		
		return descricaoResumida;
	}
	
	public void setDescricaoResumida(String descricaoResumida) {
		this.descricaoResumida = descricaoResumida;
	}

	public BigDecimal getPreco() {
		return preco;
	}

	public void setPreco(BigDecimal preco) {
		this.preco = preco;
	}
		
	public LocalDate getDataExpiracao() {
		return dataExpiracao;
	}

	public void setDataExpiracao(LocalDate dataExpiracao) {
		this.dataExpiracao = dataExpiracao;
	}
		
	public LocalDate getDataPublicacao() {
		return dataPublicacao;
	}

	public void setDataPublicacao(LocalDate dataPublicacao) {
		this.dataPublicacao = dataPublicacao;
	}
	
	public String getDataPublicacaoString() {
		if(dataPublicacao == null) {
			return "";
		}
		DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		return formatador.format(dataPublicacao);
	}
	
	public boolean isExpirado() {
		if(dataExpiracao == null) {
			return false;
		}
		
		return dataExpiracao.isBefore(LocalDate.now());
	}

	public String getExpiradoDescricao() {
		return (isExpirado() ? "Sim" : "Não");
	}
	
	public Long getQtdeInteressados() {
		return qtdeInteressados;
	}

	public void setQtdeInteressados(Long qtdeInteressados) {
		this.qtdeInteressados = qtdeInteressados;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Anuncio other = (Anuncio) obj;
		if (codigo == null) {
			if (other.codigo != null)
				return false;
		} else if (!codigo.equals(other.codigo))
			return false;
		return true;
	}
	
}
