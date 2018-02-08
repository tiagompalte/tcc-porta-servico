package br.com.utfpr.porta.modelo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import br.com.utfpr.porta.validacao.DiaMes;

@Entity
@Table(name = "autorizacao")
public class Autorizacao implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@EmbeddedId
	private AutorizacaoId id;
	
	@NotNull(message = "Escolha o tipo de autorização")
	@Enumerated(EnumType.STRING)
	@Column(name = "tipo_autorizacao")
	private TipoAutorizacao tipoAutorizacao;
	
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "dia_semana")
	private DiaSemana diaSemana;
	
	@DiaMes
	@Column(name = "dia_mes")
	private Integer diaMes;
			
	@DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
	@Column(name = "hora_inicio")
	private LocalTime horaInicio;
	
	@DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
	@Column(name = "hora_fim")
	private LocalTime horaFim;
	
	@DateTimeFormat(pattern="dd/MM/yyyy HH:mm")
	@Column(name = "data_hora_inicio")
	private LocalDateTime dataHoraInicio; 
	
	@DateTimeFormat(pattern="dd/MM/yyyy HH:mm")
	@Column(name = "data_hora_fim")
	private LocalDateTime dataHoraFim;
		
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
		
	public String getDescricao() {
		
		if(tipoAutorizacao != null && id != null && id.getPorta() != null 
				&& id.getUsuario() != null && id.getUsuario().getPessoa() != null) {
			return "autorização " + tipoAutorizacao.getDescricao().toLowerCase()
					+ " da porta '" + id.getPorta().getDescricao() + "'" 
					+ " usuário '" + id.getUsuario().getPessoa().getNome() + "'"; 
		}			
		return "autorização";
	}
	
	public String getDetalhes() {
		
		DateTimeFormatter dataHoraFormatador = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
		DateTimeFormatter horaFormatador = DateTimeFormatter.ofPattern("HH:mm");
		
		if(tipoAutorizacao != null) {
			switch(tipoAutorizacao) {
			case PERMANENTE:
				return "Qualquer dia e horário";
			case MENSAL:
				return "Todo mês no dia ".concat(diaMes.toString()).concat(": ")
						.concat(horaInicio.format(horaFormatador).concat(" até ")
						.concat(horaFim.format(horaFormatador)));
			case SEMANAL:
				return diaSemana.getDescricao().concat(": ")
							.concat(horaInicio.format(horaFormatador).concat(" até ")
							.concat(horaFim.format(horaFormatador)));
			case TEMPORARIO:
				return dataHoraInicio.format(dataHoraFormatador).concat(" até ")
							.concat(dataHoraFim.format(dataHoraFormatador));
			}
		}
		
		return "";
	}
		
	public boolean isNovo() {
		return id != null && id.getSequencia() == null;
	}

	public AutorizacaoId getId() {
		return id;
	}

	public void setId(AutorizacaoId id) {
		this.id = id;
	}

	public TipoAutorizacao getTipoAutorizacao() {
		return tipoAutorizacao;
	}

	public void setTipoAutorizacao(TipoAutorizacao tipoAutorizacao) {
		this.tipoAutorizacao = tipoAutorizacao;
	}

	public DiaSemana getDiaSemana() {
		return diaSemana;
	}

	public void setDiaSemana(DiaSemana diaSemana) {
		this.diaSemana = diaSemana;
	}

	public Integer getDiaMes() {
		return diaMes;
	}

	public void setDiaMes(Integer diaMes) {
		this.diaMes = diaMes;
	}

	public LocalTime getHoraInicio() {
		return horaInicio;
	}

	public void setHoraInicio(LocalTime horaInicio) {
		this.horaInicio = horaInicio;
	}

	public LocalTime getHoraFim() {
		return horaFim;
	}

	public void setHoraFim(LocalTime horaFim) {
		this.horaFim = horaFim;
	}

	public LocalDateTime getDataHoraInicio() {
		return dataHoraInicio;
	}

	public void setDataHoraInicio(LocalDateTime dataHoraInicio) {
		this.dataHoraInicio = dataHoraInicio;
	}

	public LocalDateTime getDataHoraFim() {
		return dataHoraFim;
	}

	public void setDataHoraFim(LocalDateTime dataHoraFim) {
		this.dataHoraFim = dataHoraFim;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Autorizacao other = (Autorizacao) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	} 	

}
