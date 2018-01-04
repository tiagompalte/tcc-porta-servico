package br.com.utfpr.porta.modelo;

public enum Genero {
	
	MASCULINO("Masculino"),
	FEMININO("Feminino"),
	OUTRO("Outro");
		
	private String descricao;
	
	Genero(String descricao) {
		this.descricao = descricao;
	}
	
	public String getDescricao() {
		return descricao;
	}
	
}
