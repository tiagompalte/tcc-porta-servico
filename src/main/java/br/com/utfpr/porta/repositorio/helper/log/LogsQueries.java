package br.com.utfpr.porta.repositorio.helper.log;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.com.utfpr.porta.modelo.Estabelecimento;
import br.com.utfpr.porta.modelo.Log;

public interface LogsQueries {
	
	public Page<Log> filtrar(Estabelecimento estabelecimento, LocalDateTime dataHoraInicio, LocalDateTime dataHoraFinal, Pageable pageable);
	
	public void apagarLogsAteData(LocalDateTime dataAte);
	
	public List<Log> filtrarSemPage(Estabelecimento estabelecimento, LocalDateTime dataHoraInicio, LocalDateTime dataHoraFinal);

}
