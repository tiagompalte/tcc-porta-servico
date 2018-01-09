package br.com.utfpr.porta.modelo;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.transaction.annotation.Transactional;

import com.mysql.jdbc.StringUtils;

@Entity
@Table(name = "parametro")
public class Parametro implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@NotBlank(message ="Código do parâmetro deve ser informado")
	private String codigo;
	
	private String descricao;
	
	@NotBlank(message ="Valor do parâmetro deve ser informado")
	private String valor;
	
	@Column(name = "data_hora_criacao", updatable=false)
	private LocalDateTime dataHoraCriacao;
	
	@Column(name = "data_hora_alteracao")
	private LocalDateTime dataHoraAlteracao;
		
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

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		if(codigo != null) {
			this.codigo = codigo.toUpperCase();
		}
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}
	
	@Transactional
	public Long getValorLong() {
		if(valor != null && StringUtils.isStrictlyNumeric(valor)) {
			return Long.parseLong(valor);
		}
		return null;
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
		Parametro other = (Parametro) obj;
		if (codigo == null) {
			if (other.codigo != null)
				return false;
		} else if (!codigo.equals(other.codigo))
			return false;
		return true;
	}
	
}
