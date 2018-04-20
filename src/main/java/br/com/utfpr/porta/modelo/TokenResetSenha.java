package br.com.utfpr.porta.modelo;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "token_reset_senha")
public class TokenResetSenha implements Serializable {
	
	private static final long serialVersionUID = 1L;
		
	@EmbeddedId
	private TokenResetSenhaId tokenResetSenhaId;
	
	@NotNull(message = "Token não pode ser nulo")
	private String token;
	
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
		
	public TokenResetSenhaId getTokenResetSenhaId() {
		return tokenResetSenhaId;
	}

	public void setTokenResetSenhaId(TokenResetSenhaId tokenResetSenhaId) {
		this.tokenResetSenhaId = tokenResetSenhaId;
	}
	
	public void setTokenResetSenhaId(Usuario usuario) {
		if(usuario != null && usuario.getCodigo() != null) {
			this.tokenResetSenhaId = new TokenResetSenhaId(usuario);
		}
	}
	
	public Usuario getUsuario() {
		if(this.tokenResetSenhaId != null) {
			return this.tokenResetSenhaId.getUsuario();
		}
		return null;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tokenResetSenhaId == null) ? 0 : tokenResetSenhaId.hashCode());
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
		TokenResetSenha other = (TokenResetSenha) obj;
		if (tokenResetSenhaId == null) {
			if (other.tokenResetSenhaId != null)
				return false;
		} else if (!tokenResetSenhaId.equals(other.tokenResetSenhaId))
			return false;
		return true;
	}

}
