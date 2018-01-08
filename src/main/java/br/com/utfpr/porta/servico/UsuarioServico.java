package br.com.utfpr.porta.servico;

import java.util.Optional;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import br.com.utfpr.porta.modelo.Grupo;
import br.com.utfpr.porta.modelo.Pessoa;
import br.com.utfpr.porta.modelo.Usuario;
import br.com.utfpr.porta.repositorio.Pessoas;
import br.com.utfpr.porta.repositorio.Usuarios;
import br.com.utfpr.porta.servico.excecao.CampoNaoInformadoExcecao;
import br.com.utfpr.porta.servico.excecao.EmailUsuarioJaCadastradoExcecao;
import br.com.utfpr.porta.servico.excecao.ImpossivelExcluirEntidadeException;
import br.com.utfpr.porta.servico.excecao.RfidUsuarioJaCadastradoExcecao;
import br.com.utfpr.porta.servico.excecao.ValidacaoBancoDadosExcecao;

@Service
public class UsuarioServico {
	
	@Autowired
	private Usuarios usuariosRepositorio;		
		
	@Autowired
	private Pessoas pessoasRepositorio;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Transactional
	public Usuario salvar(Usuario usuario) {
		
		if(usuario == null) {
			throw new NullPointerException("Entidade usuário está nulo");
		}
				
		Optional<Usuario> usuarioExistente = usuariosRepositorio.findByEmail(usuario.getEmail());
		if (usuarioExistente.isPresent() && !usuarioExistente.get().equals(usuario)) {
			throw new EmailUsuarioJaCadastradoExcecao("E-mail já cadastrado");
		}
		
		if(StringUtils.isEmpty(usuario.getSenhaSite())) {
			if (usuario.isNovo()) {
				throw new CampoNaoInformadoExcecao("senhaSite", "Senha do site é obrigatória para novo usuário");
			}
			else {
				Usuario usuarioBase = usuariosRepositorio.findOne(usuario.getCodigo());
				if(usuarioBase != null) {
					usuario.setSenhaSite(usuarioBase.getSenhaSite());
					usuario.setConfirmacaoSenhaSite(usuarioBase.getSenhaSite());					
				}
			}			
		}
		else {			
			if(usuario.getSenhaSite().length() < 6 || usuario.getSenhaSite().length() > 12) {
				throw new CampoNaoInformadoExcecao("senhaSite", "Senha do site deve ter entre 6 e 12 caracteres");
			}
			usuario.setSenhaSite(this.passwordEncoder.encode(usuario.getSenhaSite()));
			usuario.setConfirmacaoSenhaSite(usuario.getSenhaSite());			
		}
		
		if(usuario.getGrupos() != null) {			
			for(Grupo grupo : usuario.getGrupos()) {	
				
				if(grupo.getCodigo().compareTo(Long.parseLong("3")) == 0) {
					//usuário
					
					if(StringUtils.isEmpty(usuario.getRfid())) {
						throw new CampoNaoInformadoExcecao("rfid", "Código do cartão RFID é obrigatório");
					}
					
					Optional<Usuario> usuarioExistenteRFID = usuariosRepositorio.findByRfid(usuario.getRfid());
					if (usuarioExistenteRFID.isPresent() && usuarioExistenteRFID.get().getRfid().compareTo(usuario.getRfid()) == 0) {
						throw new RfidUsuarioJaCadastradoExcecao("RFID já cadastrado");
					}
					
					if(StringUtils.isEmpty(usuario.getNomeAudio())) {
						throw new CampoNaoInformadoExcecao("nomeAudio", "Senha falada não informada");
					}
					
					if(StringUtils.isEmpty(usuario.getSenhaTeclado())) {
						if (usuario.isNovo()) {
							throw new CampoNaoInformadoExcecao("senhaPorta", "Senha da porta é obrigatória para novo usuário");
						}
						else {
							Usuario usuarioBase = usuariosRepositorio.findOne(usuario.getCodigo());
							if(usuarioBase != null) {							
								usuario.setSenhaTeclado(usuarioBase.getSenhaTeclado());
								usuario.setConfirmacaoSenhaTeclado(usuarioBase.getSenhaTeclado());
							}
						}
					}
					else {					
						if(usuario.getSenhaTeclado().length() != 4) {
							throw new CampoNaoInformadoExcecao("senhaPorta", "Senha da porta deve ter 4 dígitos");
						}
						usuario.setSenhaTeclado(this.passwordEncoder.encode(usuario.getSenhaTeclado()));
						usuario.setConfirmacaoSenhaTeclado(usuario.getSenhaTeclado());
					}
					
				}
				else if(grupo.getCodigo().compareTo(Long.parseLong("2")) == 0) {
					//anfitrião
					usuario.setSenhaTeclado("");
					usuario.setConfirmacaoSenhaTeclado(usuario.getSenhaTeclado());
				}
			}
		}
		else {
			throw new NullPointerException("Grupo do usuário não informado");
		}
										
		if(usuario.getPessoa() == null) {
			throw new NullPointerException("Dados pessoais não informado");
		}
				
		Pessoa pessoaSalva = pessoasRepositorio.save(usuario.getPessoa());
		
		if(pessoaSalva == null || pessoaSalva.getCodigo() == null) {
			throw new ValidacaoBancoDadosExcecao("Não foi possível salvar os dados pessoais do usuário"); 
		}
		
		if(usuario.isNovo()) {
			usuario.setPessoa(pessoaSalva);	
			usuario.setAtivo(Boolean.TRUE);
		}
		
		return usuariosRepositorio.save(usuario);
		
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
