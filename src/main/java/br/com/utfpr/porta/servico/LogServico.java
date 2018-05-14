package br.com.utfpr.porta.servico;

import java.time.LocalDateTime;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.utfpr.porta.modelo.Acao;
import br.com.utfpr.porta.modelo.Log;
import br.com.utfpr.porta.modelo.Porta;
import br.com.utfpr.porta.modelo.TipoAutenticacao;
import br.com.utfpr.porta.modelo.Usuario;
import br.com.utfpr.porta.repositorio.Logs;
import br.com.utfpr.porta.servico.excecao.RegistrarLogExcecao;

@Service
public class LogServico {
	
	@Autowired
	private Logs logRepositorio;
		
	@Transactional
	public void entrarPorta(Usuario usuario, Porta porta, LocalDateTime dataHora, TipoAutenticacao tipoAutenticacao) {
		
		try {
			
			if(usuario == null || usuario.getCodigo() == null) {
				throw new NullPointerException("Usuário não informado");
			}
			
			if(porta == null || porta.getCodigo() == null) {
				throw new NullPointerException("Porta não informada");
			}
			
			if(porta.getEstabelecimento() == null || porta.getEstabelecimento().getCodigo() == null) {
				throw new NullPointerException("Estabelecimento não informado");
			}
			
			if(tipoAutenticacao == null || Strings.isEmpty(tipoAutenticacao.getDescricao())) {
				throw new NullPointerException("Tipo de autenticação não informado");
			}
			
			if(dataHora == null) {
				dataHora = LocalDateTime.now();
			}
			
			Log log = new Log();
			log.setDataHora(dataHora);
			log.setAcao(String.format(Acao.ENTRAR_PORTA.getDescricao(), usuario.getCodigoNome(), 
					porta.getCodigoDescricao(), tipoAutenticacao.getDescricao().toLowerCase()));
			log.setEstabelecimento(porta.getEstabelecimento());
			logRepositorio.save(log);			
		} 
		catch(Exception e) {
			throw new RegistrarLogExcecao("Erro ao registrar log.", e);
		}		
	}
}
