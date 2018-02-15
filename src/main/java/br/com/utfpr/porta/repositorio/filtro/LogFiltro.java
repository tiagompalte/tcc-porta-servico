package br.com.utfpr.porta.repositorio.filtro;

import java.time.LocalDateTime;

import javax.persistence.Column;

import org.springframework.format.annotation.DateTimeFormat;

import br.com.utfpr.porta.modelo.Estabelecimento;

public class LogFiltro {
		
	@DateTimeFormat(pattern="dd/MM/yyyy HH:mm")
	@Column(name = "data_hora_inicio")
	private LocalDateTime dataHoraInicio; 
	
	@DateTimeFormat(pattern="dd/MM/yyyy HH:mm")
	@Column(name = "data_hora_fim")
	private LocalDateTime dataHoraFim;
	
    private Estabelecimento estabelecimento;
    
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
	public Estabelecimento getEstabelecimento() {
		return estabelecimento;
	}
	public void setEstabelecimento(Estabelecimento estabelecimento) {
		this.estabelecimento = estabelecimento;
	}
	
}
