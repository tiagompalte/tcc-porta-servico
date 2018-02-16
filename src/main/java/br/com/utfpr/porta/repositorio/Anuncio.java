package br.com.utfpr.porta.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.utfpr.porta.modelo.Estabelecimento;
import br.com.utfpr.porta.repositorio.helper.anuncio.AnuncioQueries;

@Repository
public interface Anuncio extends JpaRepository<br.com.utfpr.porta.modelo.Anuncio, Long>, AnuncioQueries {
	
	public br.com.utfpr.porta.modelo.Anuncio findByCodigoAndEstabelecimento(Long codigo, Estabelecimento estabelecimento); 

}
