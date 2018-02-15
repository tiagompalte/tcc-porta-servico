package br.com.utfpr.porta.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.utfpr.porta.modelo.Anuncio;
import br.com.utfpr.porta.repositorio.helper.anuncio.AnuncioQueries;

@Repository
public interface Anuncios extends JpaRepository<Anuncio, Long>, AnuncioQueries {

}
