package br.com.utfpr.porta.storage.s3;

import java.io.IOException;
import java.io.InputStream;

import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;

import br.com.utfpr.porta.repositorio.Usuarios;
import br.com.utfpr.porta.storage.AudioStorage;

@Component
public class AudioStorageS3 implements AudioStorage {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AudioStorageS3.class);

	private static final String BUCKET = "awporta";
	
	@Autowired
	private AmazonS3 amazonS3;
	
	@Autowired
	private Usuarios usuarioRepositorio;

	@Override
	public void salvar(String name, MultipartFile file) {
		
		if (file != null && !Strings.isEmpty(name)) {			
			try {
				AccessControlList acl = new AccessControlList();
				acl.grantPermission(GroupGrantee.AllUsers, Permission.Read);
				enviarAudio(name, file, acl);
				LOGGER.info("Áudio salvo no S3 com o nome ".concat(name));
			} catch (IOException e) {
				usuarioRepositorio.apagarNomeAudio(name);
				LOGGER.error("Erro salvando arquivo no S3 ".concat(e.getMessage()));
				throw new RuntimeException("Erro salvando arquivo no S3 ", e);
			} catch(Exception e) {
				usuarioRepositorio.apagarNomeAudio(name);
				LOGGER.error(e.getMessage());
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public byte[] recuperar(String audio) {	
		
		if(Strings.isEmpty(audio)) {
			return null;
		}
		
		try {
			InputStream is = amazonS3.getObject(BUCKET, audio).getObjectContent();
			return IOUtils.toByteArray(is);
		} catch (IOException e) {
			LOGGER.error(String.format("Não conseguiu recuperar o áudio %s do S3", audio).concat(e.getMessage()));
		} catch (NullPointerException e) {
			LOGGER.error(String.format("O áudio %s não existe. ", audio).concat(e.getMessage()));
		} catch(Exception e) {
			LOGGER.error("Exceção ao recuperar áudio: ", e);
		}
		return null;
	}

	@Override
	public void excluir(String audio) {
		if(Strings.isEmpty(audio)) {
			return;
		}	
		
		try {			
			amazonS3.deleteObjects(new DeleteObjectsRequest(BUCKET).withKeys(audio));
		}
		catch(Exception e) {
			LOGGER.error("Erro ao deletar arquivo no S3", e);
		}
	}

	@Override
	public String getUrl(String audio) {
		if (!Strings.isEmpty(audio)) {
			return "https://s3-sa-east-1.amazonaws.com/".concat(BUCKET).concat("/") + audio;
		}		
		return null;
	}
		
	private ObjectMetadata enviarAudio(String novoNome, MultipartFile file, AccessControlList acl) throws IOException {
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentType(file.getContentType());
		metadata.setContentLength(file.getSize());
		amazonS3.putObject(new PutObjectRequest(BUCKET, novoNome, file.getInputStream(), metadata).withAccessControlList(acl));
		return metadata;
	}

}
