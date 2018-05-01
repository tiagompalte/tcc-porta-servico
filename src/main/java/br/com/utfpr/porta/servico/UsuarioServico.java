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
	
	private static final String PADRAO_SENHA_SITE = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%!^&*]).{6,12}$";
	private static final String PADRAO_SENHA_TECLADO = "^\\d{4}$";
	
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
	
	private void validar(Usuario usuario) {
		
		if(usuario == null) {
			throw new NullPointerException("Entidade usuário está nulo");
		}
		
		if(usuario.getPessoa() == null) {
			throw new NullPointerException("Dados pessoais não informado");
		}
		
		if(usuario.getGrupos() == null) {	
			throw new NullPointerException("Grupo do usuário não informado");
		}
		
		if(usuario.isNovo() && Strings.isEmpty(usuario.getSenhaSite())) {
			throw new CampoNaoInformadoExcecao("senhaSite", "Senha do site é obrigatória para novo usuário");
		}
		
		if(Strings.isEmpty(usuario.getEmail())) {
			throw new CampoNaoInformadoExcecao("email", "E-mail é obrigatório o preenchimento");
		}
		
		Optional<Usuario> usuarioExistente = usuariosRepositorio.findByEmail(usuario.getEmail());
		if (usuarioExistente.isPresent() && !usuarioExistente.get().equals(usuario)) {
			throw new EmailUsuarioJaCadastradoExcecao("E-mail já cadastrado");
		}
	}
	
	private void validarNovaSenhaSite(Usuario usuario) {
		if(Strings.isEmpty(usuario.getSenhaSite())) {
			throw new NullPointerException("Senha do site não informada");
		}
		
		if(!usuario.getSenhaSite().matches(PADRAO_SENHA_SITE)) {
			throw new CampoNaoInformadoExcecao("senhaSite", "A senha do site deve conter uma letra maiúscula, um caracter especial(@,#,$,%,!,^,&,*) e um número. Deve conter de 6 a 12 caracteres");
		}
		usuario.setSenhaSite(this.passwordEncoder.encode(usuario.getSenhaSite()));
		usuario.setConfirmacaoSenhaSite(usuario.getSenhaSite());		
	}
	
	private void validarNovaSenhaTeclado(Usuario usuario) {
		if(Strings.isEmpty(usuario.getSenhaTeclado())) {
			throw new NullPointerException("Senha do teclado não informada");
		}
		
		if(!usuario.getSenhaTeclado().matches(PADRAO_SENHA_TECLADO)) {
			throw new CampoNaoInformadoExcecao("senhaTeclado", "Senha da porta deve ter 4 dígitos");
		}		
		usuario.setSenhaTeclado(this.passwordEncoder.encode(usuario.getSenhaTeclado()));
		usuario.setConfirmacaoSenhaTeclado(usuario.getSenhaTeclado());
	}
	
	private void validarGrupoUsuario(Usuario usuario) {
				
		if(Strings.isEmpty(usuario.getRfid())) {
			throw new CampoNaoInformadoExcecao("rfid", "Código do cartão RFID é obrigatório");
		}
		
		Optional<Usuario> usuarioExistenteRFID = usuariosRepositorio.findByRfidAndEmailNot(usuario.getRfid(), usuario.getEmail());
		if (usuarioExistenteRFID.isPresent() && usuarioExistenteRFID.get().getRfid().compareTo(usuario.getRfid()) == 0) {
			throw new RfidUsuarioJaCadastradoExcecao("RFID já cadastrado");
		}

		if(usuario.isSenhaTecladoAlterado()) {			
			validarNovaSenhaTeclado(usuario);
		}
	}
	
	private void verificarSenhasUsuario(Usuario usuario) {
		
		if(!usuario.isNovo()) {
			
			Usuario usuarioBase = usuariosRepositorio.findOne(usuario.getCodigo());
			
			if(usuarioBase == null) {
				throw new ValidacaoBancoDadosExcecao("Não foi possível encontrar na base de dados o usuário alterado");
			}
			
			if(Strings.isEmpty(usuario.getSenhaSite())) {
				usuario.setSenhaSite(usuarioBase.getSenhaSite());
				usuario.setConfirmacaoSenhaSite(usuarioBase.getSenhaSite());			
			}	
			else {
				validarNovaSenhaSite(usuario);
			}
			
			if(Strings.isEmpty(usuario.getSenhaTeclado())) {
				usuario.setSenhaTeclado(usuarioBase.getSenhaTeclado());
				usuario.setConfirmacaoSenhaTeclado(usuarioBase.getSenhaTeclado());
				usuario.setSenhaTecladoAlterado(false);
			}
		}
		else {			
			validarNovaSenhaSite(usuario);
		}
	}
			
	@Transactional
	public Usuario salvar(Usuario usuario) {
		
		validar(usuario);
						
		verificarSenhasUsuario(usuario);
		
		Parametro parCodGrpUsuario = parametroRepositorio.findOne("COD_GRP_USUARIO");
		
		if(parCodGrpUsuario == null) {
			throw new NullPointerException("COD_GRP_USUARIO não parametrizado");
		}
		
		for(Grupo grupo : usuario.getGrupos()) {
			
			if(grupo.getCodigo().compareTo(parCodGrpUsuario.getValorLong()) == 0) {
				validarGrupoUsuario(usuario);				
			}
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
		
		if(Strings.isNotEmpty(usuario.getNomeAudio())) {
			audioStorage.excluir(usuario.getNomeAudio());
		}
		
	}
	
	@Transactional
	public void alterarSenhaSite(Usuario usuario, String novaSenha) {
		
		if(usuario == null || usuario.getCodigo() == null) {
			throw new NullPointerException("Usuário não informado");
		}
		
		usuario.setSenhaSite(novaSenha);
		
		validarNovaSenhaSite(usuario);
		
		if(Strings.isNotEmpty(usuario.getSenhaTeclado())) {
			usuario.setConfirmacaoSenhaTeclado(usuario.getSenhaTeclado());
		}
		
		try {
			usuariosRepositorio.save(usuario);			
		}
		catch(PersistenceException e) {
			throw new ValidacaoBancoDadosExcecao("Erro ao alterar senha do usuário");
		}
		
	}
	
}
