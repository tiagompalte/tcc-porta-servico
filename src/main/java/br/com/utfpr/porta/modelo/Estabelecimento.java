package br.com.utfpr.porta.modelo;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;

import org.hibernate.validator.constraints.NotBlank;

@Entity
@Table(name = "estabelecimento")
public class Estabelecimento implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long codigo;
	
	@NotBlank(message = "Nome é obrigatório")
	private String nome;
	
	@ManyToOne
	@JoinColumn(name = "codigo_endereco")
	@Valid
	private Endereco endereco;
	
	@ManyToOne
	@JoinColumn(name = "codigo_responsavel")
	@Valid
	private Pessoa responsavel;
	
	@Transient
	private Long quantidadeUsuarios;
	
	@Transient
	private Long quantidadePortas;
	
	public boolean isNovo() {
		return codigo == null;
	}
	
	public String getCodigoNome() {
		return codigo.toString() + " - " + nome;
	}

	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Endereco getEndereco() {
		return endereco;
	}

	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
	}

	public Pessoa getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(Pessoa responsavel) {
		this.responsavel = responsavel;
	}
	
	public Long getQuantidadeUsuarios() {
		return quantidadeUsuarios;
	}

	public void setQuantidadeUsuarios(Long quantidadeUsuarios) {
		this.quantidadeUsuarios = quantidadeUsuarios;
	}

	public Long getQuantidadePortas() {
		return quantidadePortas;
	}

	public void setQuantidadePortas(Long quantidadePortas) {
		this.quantidadePortas = quantidadePortas;
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
		Estabelecimento other = (Estabelecimento) obj;
		if (codigo == null) {
			if (other.codigo != null)
				return false;
		} else if (!codigo.equals(other.codigo))
			return false;
		return true;
	}
	
}
