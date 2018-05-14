package br.com.utfpr.porta.modelo;

public enum TipoAutenticacao {
	
	SENHA_DIGITADA("senha digitada"),
	SENHA_FALADA("senha falada");
	
	private String descricao;
	
	TipoAutenticacao(String descricao) {
		this.descricao = descricao;
	}
	
	public String getDescricao() {
		return descricao;
	}

}
