package br.com.utfpr.porta.servico;

import java.util.Optional;

import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UsuarioServico.class);
	
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
					
					if(!usuario.isNovo()) {	
						
						if(StringUtils.isEmpty(usuario.getRfid())) {
							throw new CampoNaoInformadoExcecao("rfid", "Código do cartão RFID é obrigatório");
						}
						
						Optional<Usuario> usuarioExistenteRFID = usuariosRepositorio.findByRfidAndEmailNot(usuario.getRfid(), usuario.getEmail());
						if (usuarioExistenteRFID.isPresent() && usuarioExistenteRFID.get().getRfid().compareTo(usuario.getRfid()) == 0) {
							throw new RfidUsuarioJaCadastradoExcecao("RFID já cadastrado");
						}
						
						if(StringUtils.isEmpty(usuario.getNomeAudio())) {
							throw new CampoNaoInformadoExcecao("nomeAudio", "Senha falada não informada");
						}
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
	
	@Transactional
	public void apagarNomeAudio(String nomeAudio) {
		
		if(StringUtils.isEmpty(nomeAudio)) {
			return;
		}
		
		try {
			Optional<Usuario> usuario = usuariosRepositorio.findByNomeAudio(nomeAudio);
			
			if(usuario.isPresent()) {
				usuario.get().setNomeAudio(null);
				usuariosRepositorio.save(usuario.get());
			}			
		}
		catch(Exception e) {
			LOGGER.error("Não foi possível apagar nome do áudio ", e);
		}
	}
	
}
