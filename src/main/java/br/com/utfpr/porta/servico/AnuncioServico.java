package br.com.utfpr.porta.servico;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.utfpr.porta.modelo.Anuncio;
import br.com.utfpr.porta.modelo.AnuncioUsuario;
import br.com.utfpr.porta.servico.excecao.CampoNaoInformadoExcecao;
import br.com.utfpr.porta.servico.excecao.InformacaoInvalidaException;
import br.com.utfpr.porta.servico.excecao.ValidacaoBancoDadosExcecao;

@Service
public class AnuncioServico {
	
	@Autowired
	private br.com.utfpr.porta.repositorio.Anuncio anuncioRepositorio;
		
	@Autowired
	private br.com.utfpr.porta.repositorio.AnuncioUsuario anuncioUsuarioRepositorio;
		
	@Transactional
	public void salvar(Anuncio anuncio) {
		
		if(anuncio == null) {
			throw new NullPointerException("Anúncio não informado");
		}
		
		Anuncio anuncioBase = null;
		if(anuncio.isNovo() == false) {
			anuncioBase = anuncioRepositorio.findOne(anuncio.getCodigo());
		}
		
		if(anuncio.getEstabelecimento() == null || anuncio.getEstabelecimento().getCodigo() == null) {
			throw new CampoNaoInformadoExcecao("estabelecimento", "Estabelecimento não informado");
		}
		
		if(anuncio.getPreco().compareTo(BigDecimal.ZERO) <= 0) {
			throw new InformacaoInvalidaException("preco", "Valor não pode ser menor igual a zero");
		}
		
		if(anuncio.getDataExpiracao() == null) {
			throw new CampoNaoInformadoExcecao("dataExpiracao", "Informe uma data de expiração do anúncio");
		}
		else if(anuncio.getDataExpiracao().isBefore(LocalDate.now())) {
			throw new CampoNaoInformadoExcecao("dataExpiracao", "Informe uma data de expiração posterior a data atual");
		}
		
		if(anuncio.isNovo()) {			
			if(anuncio.getDataPublicacao() == null) {
				throw new CampoNaoInformadoExcecao("dataPublicacao", "Informe uma data de publicação do anúncio");
			}
			else if(anuncio.getDataPublicacao().isAfter(LocalDate.now())) {
				throw new CampoNaoInformadoExcecao("dataPublicacao", "Informe uma data de publicação anterior a data atual");
			}
		}
		else {
			anuncio.setDataPublicacao(anuncioBase.getDataPublicacao());
		}
				
		anuncioRepositorio.save(anuncio);
	}
		
	@Transactional(rollbackFor=NullPointerException.class)
	public void expirarAnuncio(Anuncio anuncio, LocalDate dataAtual) {
		
		if(anuncio == null) {
			throw new NullPointerException("Anúncio não informado");
		}
		
		if(dataAtual == null) {
			dataAtual = LocalDate.now();
		}
		
		anuncio.setDataExpiracao(dataAtual.minusDays(1));
		
		anuncioRepositorio.save(anuncio);
		
	}
	
	@Transactional(rollbackFor={ValidacaoBancoDadosExcecao.class,NullPointerException.class})
	public void excluir(Anuncio anuncio) {
		
		if(anuncio == null || anuncio.getCodigo() == null) {
			throw new NullPointerException("Anúncio não informado");
		}
		
		List<AnuncioUsuario> listaAnuncioUsuario = anuncioUsuarioRepositorio.obterListaAnuncioUsuarioPorAnuncio(anuncio);
		
		if(listaAnuncioUsuario != null && listaAnuncioUsuario.isEmpty() == false) {
			for(AnuncioUsuario anuncioUsuario : listaAnuncioUsuario) {
				try {					
					anuncioUsuarioRepositorio.delete(anuncioUsuario);
				}
				catch(Exception e) {
					throw new ValidacaoBancoDadosExcecao("Erro ao excluir relação de usuários interessados pelo anúncio");
				}
			}
		}
		
		try {			
			anuncioRepositorio.delete(anuncio.getCodigo());
		}
		catch(PersistenceException e) {
			throw new ValidacaoBancoDadosExcecao("Erro ao excluir anúncio");
		}
	}

}
