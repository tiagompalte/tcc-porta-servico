package br.com.utfpr.porta.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.utfpr.porta.modelo.Parametro;

@Repository
public interface Parametros extends JpaRepository<Parametro, String> {

}
