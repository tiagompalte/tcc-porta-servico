package br.com.utfpr.porta.servico;

import java.util.Optional;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import br.com.utfpr.porta.modelo.Pessoa;
import br.com.utfpr.porta.modelo.Usuario;
import br.com.utfpr.porta.repositorio.Pessoas;
import br.com.utfpr.porta.repositorio.Usuarios;
import br.com.utfpr.porta.servico.excecao.EmailUsuarioJaCadastradoExcecao;
import br.com.utfpr.porta.servico.excecao.ImpossivelExcluirEntidadeException;
import br.com.utfpr.porta.servico.excecao.RfidUsuarioJaCadastradoExcecao;
import br.com.utfpr.porta.servico.excecao.SenhaObrigatoriaUsuarioExcecao;
import br.com.utfpr.porta.servico.excecao.ValidacaoBancoDadosExcecao;

@Service
public class UsuarioServico {
	
	@Autowired
	private Usuarios usuariosRepositorio;		
		
	@Autowired
	private Pessoas pessoasRepositorio;
	
	@Transactional
	public void salvar(Usuario usuario) {
		
		if(usuario == null) {
			throw new NullPointerException("Entidade usuário está nulo");
		}
				
		Optional<Usuario> usuarioExistente = usuariosRepositorio.findByEmail(usuario.getEmail());
		if (usuarioExistente.isPresent() && !usuarioExistente.get().equals(usuario)) {
			throw new EmailUsuarioJaCadastradoExcecao("E-mail já cadastrado");
		}
		
		Optional<Usuario> usuarioExistenteRFID = usuariosRepositorio.findByRfid(usuario.getRfid());
		if (usuarioExistenteRFID.isPresent() && !usuarioExistenteRFID.get().equals(usuario)) {
			throw new RfidUsuarioJaCadastradoExcecao("RFID já cadastrado");
		}
		
		if (usuario.isNovo() && StringUtils.isEmpty(usuario.getSenhaSite())) {
			throw new SenhaObrigatoriaUsuarioExcecao("Senha é obrigatória para novo usuário");
		}
								
		if(usuario.getPessoa() == null) {
			throw new NullPointerException("Dados pessoais não informado");
		}
		
		if(StringUtils.isEmpty(usuario.getNomeAudio())) {
			throw new NullPointerException("Senha falada não informada");
		}
		
		Pessoa pessoaSalva = pessoasRepositorio.save(usuario.getPessoa());
		
		if(pessoaSalva == null || pessoaSalva.getCodigo() == null) {
			throw new ValidacaoBancoDadosExcecao("Não foi possível salvar os dados pessoais do usuário"); 
		}
		
		if(usuario.getPessoa().isNovo()) {
			usuario.setPessoa(pessoaSalva);			
		}
		
		usuariosRepositorio.save(usuario);
		
	}
	
	@Transactional
	public void excluir(Long codigo) {
		
		if(codigo == null) {
			throw new NullPointerException("Código do usuário não informado");
		}
		
		try {
			usuariosRepositorio.delete(codigo);
			usuariosRepositorio.flush();
		}
		catch(PersistenceException e) {
			throw new ImpossivelExcluirEntidadeException("Impossível apagar usuário. Ele possui autorizações relacionadas.");
		}
		
	}
	
}
