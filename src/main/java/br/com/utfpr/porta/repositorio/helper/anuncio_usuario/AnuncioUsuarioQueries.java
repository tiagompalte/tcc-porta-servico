package br.com.utfpr.porta.repositorio.helper.anuncio_usuario;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.com.utfpr.porta.modelo.Anuncio;
import br.com.utfpr.porta.modelo.Usuario;
import br.com.utfpr.porta.repositorio.filtro.AnuncioUsuarioFiltro;

public interface AnuncioUsuarioQueries {
	
	public int excluirPorAnuncio(Anuncio anuncio);
	
	public List<Usuario> obterListaUsuariosPorAnuncio(Long codigo_anuncio);
	
	public Page<Anuncio> filtrar(AnuncioUsuarioFiltro filtro, Pageable pageable);

}
