package br.com.utfpr.porta.repositorio.helper.autorizacao;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.com.utfpr.porta.modelo.Autorizacao;
import br.com.utfpr.porta.repositorio.filtro.AutorizacaoFiltro;

public interface AutorizacoesQueries {
	
	public List<Autorizacao> findByCodigoUsuarioAndCodigoPorta(Long codigoUsuario, Long codigoPorta);
	
	public List<Autorizacao> findByCodigoPorta(Long codigoPorta);
	
	public List<Autorizacao> findByCodigoUsuario(Long codigoUsuario);
	
	public Page<Autorizacao> filtrar(AutorizacaoFiltro filtro, Pageable pageable);
	
	public void apagarAutorizacoesTemporariasVencidas(Date dataAtual);

}
