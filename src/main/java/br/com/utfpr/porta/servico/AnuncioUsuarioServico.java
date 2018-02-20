package br.com.utfpr.porta.servico;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.utfpr.porta.modelo.Anuncio;
import br.com.utfpr.porta.modelo.AnuncioUsuario;
import br.com.utfpr.porta.modelo.AnuncioUsuarioId;
import br.com.utfpr.porta.modelo.Usuario;
import br.com.utfpr.porta.repositorio.Usuarios;
import br.com.utfpr.porta.servico.excecao.ValidacaoBancoDadosExcecao;

@Service
public class AnuncioUsuarioServico {
	
	@Autowired
	private br.com.utfpr.porta.repositorio.Anuncio anuncioRepositorio;
		
	@Autowired
	private br.com.utfpr.porta.repositorio.AnuncioUsuario anuncioUsuarioRepositorio;
	
	@Autowired
	private Usuarios usuariosRepositorio;
	
	private void validacao(Long codigo_anuncio, Long codigo_usuario) {
		
		if(codigo_anuncio == null) {
			throw new NullPointerException("Código do anúncio não informado");
		}
		
		if(codigo_usuario == null) {
			throw new NullPointerException("Código do usuário não informado");
		}
		
	}
	
	@Transactional
	public void adicionarUsuarioInteressado(Long codigo_anuncio, Long codigo_usuario) {
		
		validacao(codigo_anuncio, codigo_usuario);
		
		Anuncio anuncio = anuncioRepositorio.findOne(codigo_anuncio);
		
		if(anuncio == null) {
			throw new NullPointerException("Anúncio não encontrado na base de dados");
		}
		
		if(anuncio.isExpirado()) {
			throw new ValidacaoBancoDadosExcecao("Anúncio expirado");
		}
		
		Usuario usuario = usuariosRepositorio.findOne(codigo_usuario);
		
		if(usuario == null) {
			throw new NullPointerException("Usuário não encontrado na base de dados");
		}
		
		AnuncioUsuarioId id = new AnuncioUsuarioId(usuario, anuncio);
		AnuncioUsuario anuncio_usuario = new AnuncioUsuario(id);
		
		anuncioUsuarioRepositorio.save(anuncio_usuario);		
	}
	
	@Transactional
	public void retirarInteresseUsuario(Long codigo_anuncio, Long codigo_usuario) {
		
		validacao(codigo_anuncio, codigo_usuario);
		
		Anuncio anuncio = anuncioRepositorio.findOne(codigo_anuncio);
		
		if(anuncio == null) {
			throw new NullPointerException("Anúncio não encontrado na base de dados");
		}
		
		if(anuncio.isExpirado()) {
			throw new ValidacaoBancoDadosExcecao("Anúncio expirado");
		}
		
		Usuario usuario = usuariosRepositorio.findOne(codigo_usuario);
		
		if(usuario == null) {
			throw new NullPointerException("Usuário não encontrado na base de dados");
		}
		
		AnuncioUsuarioId id = new AnuncioUsuarioId(usuario, anuncio);
		
		anuncioUsuarioRepositorio.delete(id);	
		
	}

}
