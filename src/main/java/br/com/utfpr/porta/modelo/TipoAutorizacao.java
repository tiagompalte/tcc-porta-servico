package br.com.utfpr.porta.modelo;

public enum TipoAutorizacao {
	
	PERMANENTE("Permanete"),
	TEMPORARIO("Temporário"),
	SEMANAL("Semanal"),
	MENSAL("Mensal");
	
	private String descricao;
	
	TipoAutorizacao(String descricao) {
		this.descricao = descricao;
	}
	
	public String getDescricao() {
		return descricao;
	}

}
