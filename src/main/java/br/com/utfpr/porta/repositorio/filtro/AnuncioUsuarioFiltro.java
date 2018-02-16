package br.com.utfpr.porta.repositorio.filtro;

import java.math.BigDecimal;

public class AnuncioUsuarioFiltro {
	
	private String estado;
	private String cidade;
	private BigDecimal faixaPrecoInicial;
	private BigDecimal faixaPrecoFinal;
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	public String getCidade() {
		return cidade;
	}
	public void setCidade(String cidade) {
		this.cidade = cidade;
	}
	public BigDecimal getFaixaPrecoInicial() {
		return faixaPrecoInicial;
	}
	public void setFaixaPrecoInicial(BigDecimal faixaPrecoInicial) {
		this.faixaPrecoInicial = faixaPrecoInicial;
	}
	public BigDecimal getFaixaPrecoFinal() {
		return faixaPrecoFinal;
	}
	public void setFaixaPrecoFinal(BigDecimal faixaPrecoFinal) {
		this.faixaPrecoFinal = faixaPrecoFinal;
	}
	
}
