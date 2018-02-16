package br.com.utfpr.porta.repositorio.helper.anuncio;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.com.utfpr.porta.modelo.Anuncio;
import br.com.utfpr.porta.repositorio.filtro.AnuncioFiltro;

public interface AnuncioQueries {
	
	public Page<Anuncio> filtrar(AnuncioFiltro filtro, Pageable pageable);

}
