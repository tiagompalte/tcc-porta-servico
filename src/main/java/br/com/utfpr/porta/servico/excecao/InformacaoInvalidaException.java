package br.com.utfpr.porta.servico.excecao;

public class InformacaoInvalidaException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private String campo;
	
	public InformacaoInvalidaException(String campo,  String msg) {
		super(msg);
		this.campo = campo;
	}
	
	public String getCampo() {
		return campo;
	}
}
