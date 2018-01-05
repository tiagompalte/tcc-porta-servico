package br.com.utfpr.porta.servico.excecao;

public class EnderecoJaCadastradoExcecao extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	public EnderecoJaCadastradoExcecao(String msg) {
		super(msg);
	}

}
