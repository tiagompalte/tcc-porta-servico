package br.com.utfpr.porta.modelo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import br.com.utfpr.porta.validacao.AtributoConfirmacao;
import br.com.utfpr.porta.validacao.AtributosConfirmacoes;

@Entity
@Table(name = "usuario")
@AtributosConfirmacoes({
	@AtributoConfirmacao(atributo="senhaSite", atributoConfirmacao="confirmacaoSenhaSite", message="Confirmação da senha do site não confere"),
	@AtributoConfirmacao(atributo="senhaTeclado", atributoConfirmacao="confirmacaoSenhaTeclado", message="Confirmação da senha da porta não confere")
})
public class Usuario implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long codigo;
	
	@ManyToOne
	@JoinColumn(name = "codigo_pessoa", nullable=false)
	@Valid
	private Pessoa pessoa;
			
	@NotBlank(message = "E-mail é obrigatório")
	@Email(message = "E-mail inválido")
	private String email;
	
	private Boolean ativo;
	
	@Column(name = "senha_site")
	private String senhaSite;
	
	@Transient
	private String confirmacaoSenhaSite;
	
	@Column(name = "senha_teclado")
	private String senhaTeclado;
	
	@Transient
	private String confirmacaoSenhaTeclado;
	
	@Transient
	private boolean senhaTecladoAlterado;

	@ManyToMany
	@JoinTable(name = "usuario_grupo", joinColumns = @JoinColumn(name = "codigo_usuario"), 
				inverseJoinColumns = @JoinColumn(name = "codigo_grupo"))
	private List<Grupo> grupos;
	
	@OneToOne
	@JoinColumn(name = "codigo_estabelecimento")
	private Estabelecimento estabelecimento;
		
	private String rfid;
			
	@Column(name = "nome_audio")
	private String nomeAudio;
	
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
		this.confirmacaoSenhaSite = senhaSite;
		this.confirmacaoSenhaTeclado = senhaTeclado;
	}
	
	public Usuario() {
		this.pessoa = new Pessoa();
		this.pessoa.setTipoPessoa(TipoPessoa.FISICA);		
	}
	
	public String getCodigoNome() {
		if(pessoa == null) {			
			return codigo.toString();
		}
		return codigo.toString().concat(" - ").concat(pessoa.getNome()); 
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
			
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public String getSenhaSite() {
		return senhaSite;
	}

	public void setSenhaSite(String senhaSite) {
		this.senhaSite = senhaSite;
	}
	
	public String getConfirmacaoSenhaSite() {
		return confirmacaoSenhaSite;
	}

	public void setConfirmacaoSenhaSite(String confirmacaoSenhaSite) {
		this.confirmacaoSenhaSite = confirmacaoSenhaSite;
	}
			
	public String getSenhaTeclado() {
		return senhaTeclado;
	}

	public void setSenhaTeclado(String senhaTeclado) {
		this.senhaTeclado = senhaTeclado;
	}

	public String getConfirmacaoSenhaTeclado() {
		return confirmacaoSenhaTeclado;
	}

	public void setConfirmacaoSenhaTeclado(String confirmacaoSenhaTeclado) {
		this.confirmacaoSenhaTeclado = confirmacaoSenhaTeclado;
	}

	public List<Grupo> getGrupos() {
		return grupos;
	}

	public void setGrupos(List<Grupo> grupos) {
		this.grupos = grupos;
	}
	
	public String getRfid() {
		return rfid;
	}

	public void setRfid(String rfid) {
		this.rfid = rfid;
	}
	
	public String getNomeAudio() {
		return nomeAudio;
	}
	
	public void setNomeAudio(String nomeAudio) {
		this.nomeAudio = nomeAudio;
	}
	
	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}
	
	public Estabelecimento getEstabelecimento() {
		return estabelecimento;
	}

	public void setEstabelecimento(Estabelecimento estabelecimento) {
		this.estabelecimento = estabelecimento;
	}
		
	public boolean isSenhaTecladoAlterado() {
		return senhaTecladoAlterado;
	}

	public void setSenhaTecladoAlterado(boolean senhaTecladoAlterado) {
		this.senhaTecladoAlterado = senhaTecladoAlterado;
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
		Usuario other = (Usuario) obj;
		if (codigo == null) {
			if (other.codigo != null)
				return false;
		} else if (!codigo.equals(other.codigo))
			return false;
		return true;
	}
	
}
