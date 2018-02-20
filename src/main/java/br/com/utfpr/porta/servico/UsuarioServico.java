package br.com.utfpr.porta.servico;

import java.util.Optional;

import javax.persistence.PersistenceException;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.utfpr.porta.modelo.Grupo;
import br.com.utfpr.porta.modelo.Parametro;
import br.com.utfpr.porta.modelo.Pessoa;
import br.com.utfpr.porta.modelo.Usuario;
import br.com.utfpr.porta.repositorio.Parametros;
import br.com.utfpr.porta.repositorio.Pessoas;
import br.com.utfpr.porta.repositorio.Usuarios;
import br.com.utfpr.porta.servico.excecao.CampoNaoInformadoExcecao;
import br.com.utfpr.porta.servico.excecao.EmailUsuarioJaCadastradoExcecao;
import br.com.utfpr.porta.servico.excecao.ImpossivelExcluirEntidadeException;
import br.com.utfpr.porta.servico.excecao.RfidUsuarioJaCadastradoExcecao;
import br.com.utfpr.porta.servico.excecao.ValidacaoBancoDadosExcecao;
import br.com.utfpr.porta.storage.AudioStorage;

@Service
public class UsuarioServico {
	
	@Autowired
	private Usuarios usuariosRepositorio;		
		
	@Autowired
	private Pessoas pessoasRepositorio;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private Parametros parametroRepositorio;
	
	@Autowired
	private AudioStorage audioStorage;
		
	@Transactional
	public Usuario salvar(Usuario usuario) {
		
		if(usuario == null) {
			throw new NullPointerException("Entidade usuário está nulo");
		}
				
		Optional<Usuario> usuarioExistente = usuariosRepositorio.findByEmail(usuario.getEmail());
		if (usuarioExistente.isPresent() && !usuarioExistente.get().equals(usuario)) {
			throw new EmailUsuarioJaCadastradoExcecao("E-mail já cadastrado");
		}
		
		Usuario usuarioBase = null;
		if(!usuario.isNovo()) {
			usuarioBase = usuariosRepositorio.findOne(usuario.getCodigo());
		}
		
		if(Strings.isEmpty(usuario.getSenhaSite())) {
			if (usuario.isNovo()) {
				throw new CampoNaoInformadoExcecao("senhaSite", "Senha do site é obrigatória para novo usuário");
			}
			else if(usuarioBase != null) {
				usuario.setSenhaSite(usuarioBase.getSenhaSite());
				usuario.setConfirmacaoSenhaSite(usuarioBase.getSenhaSite());
			}			
		}
		else {			
			if(usuario.getSenhaSite().matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%!^&*]).{6,12}$") == false) {
				throw new CampoNaoInformadoExcecao("senhaSite", "A senha do site deve conter uma letra maiúscula, um caracter especial(@,#,$,%,!,^,&,*) e um número. Deve conter de 6 a 12 dígitos");
			}
			usuario.setSenhaSite(this.passwordEncoder.encode(usuario.getSenhaSite()));
			usuario.setConfirmacaoSenhaSite(usuario.getSenhaSite());			
		}
		
		if(usuario.getGrupos() != null) {		
			
			Parametro par_cod_grp_anfitricao = parametroRepositorio.findOne("COD_GRP_ANFITRIAO");
			Parametro par_cod_grp_usuario = parametroRepositorio.findOne("COD_GRP_USUARIO");
			
			if(par_cod_grp_usuario == null) {
				throw new NullPointerException("COD_GRP_USUARIO não parametrizado");
			}
			
			if(par_cod_grp_anfitricao == null){
				throw new NullPointerException("COD_GRP_ANFITRIAO não parametrizado");
			}
			
			for(Grupo grupo : usuario.getGrupos()) {	
				
				if(grupo.getCodigo().compareTo(par_cod_grp_usuario.getValorLong()) == 0) {
					//usuário
					
					usuario.setEstabelecimento(null);
					
					if(Strings.isEmpty(usuario.getRfid())) {
						throw new CampoNaoInformadoExcecao("rfid", "Código do cartão RFID é obrigatório");
					}
					
					Optional<Usuario> usuarioExistenteRFID = usuariosRepositorio.findByRfidAndEmailNot(usuario.getRfid(), usuario.getEmail());
					if (usuarioExistenteRFID.isPresent() && usuarioExistenteRFID.get().getRfid().compareTo(usuario.getRfid()) == 0) {
						throw new RfidUsuarioJaCadastradoExcecao("RFID já cadastrado");
					}
					
//					if(Strings.isEmpty(usuario.getNomeAudio())) {
//						throw new CampoNaoInformadoExcecao("nomeAudio", "Senha falada não informada");
//					}
					
					if(Strings.isEmpty(usuario.getSenhaTeclado())) {
						if (usuario.isNovo()) {
							throw new CampoNaoInformadoExcecao("senhaPorta", "Senha da porta é obrigatória para novo usuário");
						}
						else if(usuarioBase != null) {							
							usuario.setSenhaTeclado(usuarioBase.getSenhaTeclado());
							usuario.setConfirmacaoSenhaTeclado(usuarioBase.getSenhaTeclado());							
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
				else if(grupo.getCodigo().compareTo(par_cod_grp_anfitricao.getValorLong()) == 0) {
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
			usuario.setNrTentativaAcessoPorta(0);
			usuario.setNrTentativaAcessoSite(0);
		}
		
		return usuariosRepositorio.save(usuario);
		
	}
	
	@Transactional
	public void excluir(Long codigo) {
		
		if(codigo == null) {
			throw new NullPointerException("Código do usuário não informado");
		}
		
		Usuario usuario = usuariosRepositorio.findOne(codigo);
		
		if(usuario == null) {
			throw new ImpossivelExcluirEntidadeException("Usuário não encontrado na base de dados");
		}
		
		try {
			usuariosRepositorio.delete(usuario);
			usuariosRepositorio.flush();
		}
		catch(PersistenceException e) {
			throw new ImpossivelExcluirEntidadeException("Impossível apagar usuário. Ele possui autorizações relacionadas.");
		}
		
		if(Strings.isEmpty(usuario.getNomeAudio()) == false) {
			audioStorage.excluir(usuario.getNomeAudio());
		}
		
	}
		
	@Transactional
	public String incrementarNrTentativaAcessoPorta(Long codigoUsuario) {
		
		if(codigoUsuario == null) {
			throw new NullPointerException("Código do usuário não informado");
		}
		
		Usuario usuario = usuariosRepositorio.findOne(codigoUsuario);
		
		if(usuario == null) {
			throw new NullPointerException("Usuário não encontrado");
		}
		
		Integer nrTentativaAtual = usuario.incrementarNrTentativaAcessoPorta(1);
		
		Parametro parTentativaMaxAcessoPorta = parametroRepositorio.findOne("NR_TENTATIVA_MAX_PORTA");
		
		String retorno = null;
		if(parTentativaMaxAcessoPorta != null && parTentativaMaxAcessoPorta.getValorLong() != null) {
			
			retorno = nrTentativaAtual.toString().concat(" de ").concat(parTentativaMaxAcessoPorta.getValor()).concat(" tentativas");
			
			if(parTentativaMaxAcessoPorta.getValorLong().compareTo(nrTentativaAtual.longValue()) <= 0) {				
				usuario.setAtivo(false);
				usuario.setNrTentativaAcessoPorta(0);
				retorno = "Usuário bloqueado";
			}
		}
		
		usuariosRepositorio.save(usuario);
		
		return retorno;
	}
	
	@Transactional
	public Usuario zerarNrTentativaAcessoPorta(Long codigoUsuario) {
		
		if(codigoUsuario == null) {
			throw new NullPointerException("Código do usuário não informado");
		}
		
		Usuario usuario = usuariosRepositorio.findOne(codigoUsuario);
		
		if(usuario == null) {
			throw new NullPointerException("Usuário não encontrado");
		}
		
		usuario.setNrTentativaAcessoPorta(0);
		
		return usuariosRepositorio.save(usuario);
	}
	
	@Transactional
	public String incrementarNrTentativaAcessoSite(Long codigoUsuario) {
		
		if(codigoUsuario == null) {
			throw new NullPointerException("Código do usuário não informado");
		}
		
		Usuario usuario = usuariosRepositorio.findOne(codigoUsuario);
		
		if(usuario == null) {
			throw new NullPointerException("Usuário não encontrado");
		}
		
		Integer nrTentativaAtual = usuario.incrementarNrTentativaAcessoSite(1);
		
		Parametro parTentativaMaxAcessoSite = parametroRepositorio.findOne("NR_TENTATIVA_MAX_SITE");
		
		String retorno = null;
		if(parTentativaMaxAcessoSite != null && parTentativaMaxAcessoSite.getValorLong() != null) {
			
			retorno = nrTentativaAtual.toString().concat(" de ").concat(parTentativaMaxAcessoSite.getValor()).concat(" tentativas");
			
			if(parTentativaMaxAcessoSite.getValorLong().compareTo(nrTentativaAtual.longValue()) <= 0) {				
				usuario.setAtivo(false);
				usuario.setNrTentativaAcessoSite(0);
				retorno = "Usuário bloqueado";
			}
		}
		
		usuariosRepositorio.save(usuario);
		
		return retorno;
	}
	
	@Transactional
	public Usuario zerarNrTentativaAcessoSite(Long codigoUsuario) {
		
		if(codigoUsuario == null) {
			throw new NullPointerException("Código do usuário não informado");
		}
		
		Usuario usuario = usuariosRepositorio.findOne(codigoUsuario);
		
		if(usuario == null) {
			throw new NullPointerException("Usuário não encontrado");
		}
		
		usuario.setNrTentativaAcessoSite(0);
		
		return usuariosRepositorio.save(usuario);
	}
	
}
