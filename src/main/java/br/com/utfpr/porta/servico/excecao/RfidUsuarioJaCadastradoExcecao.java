package br.com.utfpr.porta.servico.excecao;

public class RfidUsuarioJaCadastradoExcecao extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	public RfidUsuarioJaCadastradoExcecao(String message) {
		super(message);
	}

}
