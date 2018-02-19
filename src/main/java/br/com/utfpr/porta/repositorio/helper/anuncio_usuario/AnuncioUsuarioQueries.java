package br.com.utfpr.porta.repositorio.helper.anuncio_usuario;

import java.util.List;

import br.com.utfpr.porta.modelo.Anuncio;
import br.com.utfpr.porta.modelo.Usuario;

public interface AnuncioUsuarioQueries {
	
	public int excluirPorAnuncio(Anuncio anuncio);
	
	public List<Usuario> obterListaUsuariosPorAnuncio(Long codigo_anuncio);

}
