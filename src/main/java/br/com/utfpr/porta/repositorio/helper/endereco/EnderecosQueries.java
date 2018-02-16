package br.com.utfpr.porta.repositorio.helper.endereco;

import java.util.List;

public interface EnderecoQueries {
	
	public List<String> obterEstados();
	
	public List<String> obterCidadesPorEstado(String estado);

}
