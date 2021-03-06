package br.com.utfpr.porta.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.utfpr.porta.modelo.Grupo;

@Repository
public interface Grupos extends JpaRepository<Grupo, Long> {
	
	public Grupo findByNome(String nome);
	
	public Grupo findByCodigo(Long codigo);
	
}
