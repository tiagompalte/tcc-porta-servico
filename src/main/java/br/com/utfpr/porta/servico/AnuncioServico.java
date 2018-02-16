package br.com.utfpr.porta.servico;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.utfpr.porta.modelo.Anuncio;
import br.com.utfpr.porta.modelo.AnuncioUsuario;
import br.com.utfpr.porta.modelo.AnuncioUsuarioId;
import br.com.utfpr.porta.modelo.Usuario;
import br.com.utfpr.porta.repositorio.Usuarios;
import br.com.utfpr.porta.servico.excecao.CampoNaoInformadoExcecao;
import br.com.utfpr.porta.servico.excecao.InformacaoInvalidaException;
import br.com.utfpr.porta.servico.excecao.ValidacaoBancoDadosExcecao;

@Service
public class AnuncioServico {
	
	@Autowired
	private br.com.utfpr.porta.repositorio.Anuncio anuncioRepositorio;
		
	@Autowired
	private br.com.utfpr.porta.repositorio.AnuncioUsuario anuncioUsuarioRepositorio;
	
	@Autowired
	private Usuarios usuariosRepositorio;
	
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
		
//		if(anuncio.getPreco().compareTo(BigDecimal.ZERO) <= 0) {
//			throw new InformacaoInvalidaException("preco", "Valor não pode ser menor igual a zero");
//		}
		
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
	
	@Transactional
	public void adicionarUsuarioInteressado(Long codigo_anuncio, Long codigo_usuario) {
		
		if(codigo_anuncio == null) {
			throw new NullPointerException("Código do anúncio não informado");
		}
		
		if(codigo_usuario == null) {
			throw new NullPointerException("Código do usuário não informado");
		}
		
		Anuncio anuncio = anuncioRepositorio.findOne(codigo_anuncio);
		
		if(anuncio == null) {
			throw new NullPointerException("Anúncio não encontrado na base de dados");
		}
		
		Usuario usuario = usuariosRepositorio.findOne(codigo_usuario);
		
		if(usuario == null) {
			throw new NullPointerException("Usuário não encontrado na base de dados");
		}
		
		AnuncioUsuarioId id = new AnuncioUsuarioId(usuario, anuncio);
		AnuncioUsuario anuncio_usuario = new AnuncioUsuario(id);
		
		anuncioUsuarioRepositorio.save(anuncio_usuario);
		
	}
	
	@Transactional(rollbackFor={ValidacaoBancoDadosExcecao.class,NullPointerException.class})
	public void excluir(Anuncio anuncio) {
		
		if(anuncio == null || anuncio.getCodigo() == null) {
			throw new NullPointerException("Anúncio não informado");
		}
		
		anuncioUsuarioRepositorio.excluirPorAnuncio(anuncio);
		
		try {			
			anuncioRepositorio.delete(anuncio);
		}
		catch(PersistenceException e) {
			throw new ValidacaoBancoDadosExcecao("Erro ao excluir anúncio");
		}
	}

}
