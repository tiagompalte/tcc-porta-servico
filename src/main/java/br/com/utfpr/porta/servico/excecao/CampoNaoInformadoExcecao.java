package br.com.utfpr.porta.servico.excecao;

public class CampoNaoInformadoExcecao extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	private String campo;
	
	public CampoNaoInformadoExcecao(String campo, String message) {		
		super(message);
		this.campo = campo;
	}
	
	public String getCampo() {
		return campo;
	}

}
