package br.com.utfpr.porta.storage.local;

import static java.nio.file.FileSystems.getDefault;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import br.com.utfpr.porta.modelo.Parametro;
import br.com.utfpr.porta.repositorio.Parametros;
import br.com.utfpr.porta.repositorio.Usuarios;
import br.com.utfpr.porta.storage.AudioStorage;

@Component
public class AudioStorageLocal implements AudioStorage {

	private static final Logger LOGGER = LoggerFactory.getLogger(AudioStorageLocal.class);
	
	private Path local;
	
	@Autowired
	private Usuarios usuarioRepositorio;
	
	@Autowired
	private Parametros parametroRepositorio;
	
	public AudioStorageLocal() {
		String os = System.getProperty("os.name").toLowerCase();
		if(!Strings.isEmpty(os) && os.indexOf("win") >= 0) {
			this.local = getDefault().getPath(System.getenv("USERPROFILE"), ".portaaudios");
		}
		else {
			this.local = getDefault().getPath(System.getenv("HOME"), ".portaaudios");
		}
		criarPastas();
	}
	
	public AudioStorageLocal(Path path) {
		this.local = path;
		criarPastas();
	}

	@Override
	public void salvar(String name, MultipartFile file) {
		if (file != null && !Strings.isEmpty(name)) {
			try {
				//Verifica se o áudio já existe na pasta, para gravar uma nova senha falada no lugar, nos casos de modificação
				Files.deleteIfExists(this.local.resolve(name));
				file.transferTo(new File(this.local.toAbsolutePath().toString() + getDefault().getSeparator() + name));
			} catch (IOException e) {
				usuarioRepositorio.apagarNomeAudio(name);
				throw new RuntimeException("Erro ao salvar o áudio. ", e);
			} catch(Exception e) {
				usuarioRepositorio.apagarNomeAudio(name);
				throw new RuntimeException("Erro ao salvar o áudio. ", e);
			}
		}
	}
	
	@Override
	public byte[] recuperar(String nome) {
		try {
			return Files.readAllBytes(this.local.resolve(nome));
		} catch (IOException e) {
			throw new RuntimeException("Erro lendo o áudio. ", e);
		}
	}
	
	@Override
	public void excluir(String audio) {
		try {
			Files.deleteIfExists(this.local.resolve(audio));
		} catch (IOException e) {
			LOGGER.warn(String.format("Erro apagando áudio '%s'. Mensagem: %s", audio, e.getMessage()));
		}		
	}
	
	@Override
	public String getUrl(String audio) {
		
		Parametro parUrlAudio = parametroRepositorio.findOne("AUDIO_URL");
		
		if(parUrlAudio == null || Strings.isEmpty(parUrlAudio.getValor())) {
			throw new RuntimeException("Parâmetro da URL de áudio não cadastrado");
		}
		
		return parUrlAudio.getValor().concat("/audios/").concat(audio);
	}
	
	private void criarPastas() {
		try {
			Files.createDirectories(this.local);
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Pasta criada para salvar áudios.");
				LOGGER.debug("Pasta default: " + this.local.toAbsolutePath());
			}
		} catch (IOException e) {
			throw new RuntimeException("Erro criando pasta para salvar áudio", e);
		}
	}
	
}
