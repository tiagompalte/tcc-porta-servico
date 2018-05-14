package br.com.utfpr.porta.servico.excecao;

public class RegistrarLogExcecao extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public RegistrarLogExcecao(String mensagem) {
		super(mensagem);
	}
	
	public RegistrarLogExcecao(String mensagem, Throwable e) {
		super(mensagem, e);
	}

}
