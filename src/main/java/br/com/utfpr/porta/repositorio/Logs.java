package br.com.utfpr.porta.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.utfpr.porta.modelo.Estabelecimento;
import br.com.utfpr.porta.modelo.Log;
import br.com.utfpr.porta.repositorio.helper.log.LogsQueries;

@Repository
public interface Logs extends JpaRepository<Log, Long>, LogsQueries {
	
	public List<Log> findByEstabelecimento(Estabelecimento estabelecimento);
	
}
