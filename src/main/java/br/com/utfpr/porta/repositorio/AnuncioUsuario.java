package br.com.utfpr.porta.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.utfpr.porta.modelo.AnuncioUsuario;
import br.com.utfpr.porta.modelo.AnuncioUsuarioId;
import br.com.utfpr.porta.repositorio.helper.anuncio_usuario.AnuncioUsuarioQueries;

@Repository
public interface AnuncioUsuarios extends JpaRepository<AnuncioUsuario, AnuncioUsuarioId>, AnuncioUsuarioQueries {

}

