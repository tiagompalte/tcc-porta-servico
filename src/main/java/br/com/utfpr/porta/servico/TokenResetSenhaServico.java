package br.com.utfpr.porta.servico;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.UUID;

import javax.persistence.PersistenceException;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.utfpr.porta.modelo.TokenResetSenha;
import br.com.utfpr.porta.modelo.Usuario;
import br.com.utfpr.porta.repositorio.TokenResetSenhas;
import br.com.utfpr.porta.repositorio.Usuarios;
import br.com.utfpr.porta.servico.excecao.ErroGerarHashExcecao;
import br.com.utfpr.porta.servico.excecao.ImpossivelExcluirEntidadeException;

@Service
public class TokenResetSenhaServico {
			
	@Autowired
	private TokenResetSenhas tokenResetSenhasRepositorio;
	
	@Autowired
	private Usuarios usuarioRepositorio;
		
	@Transactional
	public TokenResetSenha gravarToken(String email) {
		
		if(Strings.isEmpty(email)) {
			throw new NullPointerException("E-mail não informado");
		}
		
		Optional<Usuario> usuario = usuarioRepositorio.findByEmail(email);
		
		if(!usuario.isPresent() || usuario.get().getCodigo() == null) {
			return null;
		}
		
		TokenResetSenha token = new TokenResetSenha();
		token.setTokenResetSenhaId(usuario.get());
		token.setToken(stringHexa(gerarHash(UUID.randomUUID().toString())));
		
		return tokenResetSenhasRepositorio.save(token);
	}
	
	private static byte[] gerarHash(String texto) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			throw new ErroGerarHashExcecao("Erro ao gerar hash");
		}
	    md.update(texto.getBytes());
	    return md.digest();
	}
	
	private static String stringHexa(byte[] bytes) {
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			int parteAlta = ((bytes[i] >> 4) & 0xf) << 4;
			int parteBaixa = bytes[i] & 0xf;
			if (parteAlta == 0)
				s.append('0');
			s.append(Integer.toHexString(parteAlta | parteBaixa));
		}
		return s.toString();
	}
	
	@Transactional
	public void excluir(TokenResetSenha token) {
		
		if(token == null || token.getTokenResetSenhaId() == null) {
			throw new NullPointerException("Token não informado");
		}
				
		try {
			tokenResetSenhasRepositorio.delete(token);
			tokenResetSenhasRepositorio.flush();
		}
		catch(PersistenceException e) {
			throw new ImpossivelExcluirEntidadeException("Não foi possível apagar token");
		}		
	}

}
