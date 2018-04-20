package br.com.utfpr.porta.servico.excecao;

public class ErroGerarHashExcecao extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public ErroGerarHashExcecao(String mensagem) {
		super(mensagem);
	}

}
