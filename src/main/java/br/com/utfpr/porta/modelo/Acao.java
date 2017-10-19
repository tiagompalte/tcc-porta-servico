package br.com.utfpr.porta.modelo;

public enum Acao {
	
	ENTRAR_PORTA("Entrada do usuário %s na porta %s. Autorizado por senha %s");
	
	private String descricao;
	
	Acao(String descricao) {
		this.descricao = descricao;
	}
	
	public String getDescricao() {
		return descricao;
	}

}
