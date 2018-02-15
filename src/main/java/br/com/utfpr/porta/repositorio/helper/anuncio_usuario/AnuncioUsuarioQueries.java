package br.com.utfpr.porta.repositorio.helper.anuncio_usuario;

import br.com.utfpr.porta.modelo.Anuncio;

public interface AnuncioUsuarioQueries {
	
	public int excluirPorAnuncio(Anuncio anuncio);
	
	public Long obterQtdeUsuariosInteressadosPorAnuncio(Long codigo_anuncio);

}
