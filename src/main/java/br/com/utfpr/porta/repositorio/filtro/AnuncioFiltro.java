package br.com.utfpr.porta.repositorio.filtro;

import java.time.LocalDate;

import br.com.utfpr.porta.modelo.Estabelecimento;

public class AnuncioFiltro {
	
	private Estabelecimento estabelecimento;
	private LocalDate dataInicio;
	private LocalDate dataFinal;
	private boolean expirado;
	
	public Estabelecimento getEstabelecimento() {
		return estabelecimento;
	}
	public void setEstabelecimento(Estabelecimento estabelecimento) {
		this.estabelecimento = estabelecimento;
	}
	public LocalDate getDataInicio() {
		return dataInicio;
	}
	public void setDataInicio(LocalDate dataInicio) {
		this.dataInicio = dataInicio;
	}
	public LocalDate getDataFinal() {
		return dataFinal;
	}
	public void setDataFinal(LocalDate dataFinal) {
		this.dataFinal = dataFinal;
	}
	public boolean isExpirado() {
		return expirado;
	}
	public void setExpirado(boolean expirado) {
		this.expirado = expirado;
	}
	
}
