package br.com.utfpr.porta.storage.s3;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;

import br.com.utfpr.porta.storage.AudioStorage;

@Component
public class AudioStorageS3 implements AudioStorage {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AudioStorageS3.class);

	private static final String BUCKET = "awporta";
	
	@Autowired
	private AmazonS3 amazonS3;

	@Override
	public void salvarTemporariamente(String name, MultipartFile file) {
		
		if (file != null && !StringUtils.isEmpty(name)) {
			
			try {
				AccessControlList acl = new AccessControlList();
				acl.grantPermission(GroupGrantee.AllUsers, Permission.Read);				
				enviarAudio(name, file, acl);
			} catch (IOException e) {
				throw new RuntimeException("Erro salvando arquivo no S3", e);
			}
		}
	}

	@Override
	public byte[] recuperarAudioTemporaria(String nome) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void salvar(String audio) {
		// TODO Auto-generated method stub

	}

	@Override
	public byte[] recuperar(String audio) {
		InputStream is = amazonS3.getObject(BUCKET, audio).getObjectContent();
		try {
			return IOUtils.toByteArray(is);
		} catch (IOException e) {
			LOGGER.error("Não conseguiu recuperar áudio do S3", e);
		}
		return null;
	}

	@Override
	public void excluir(String audio) {
		amazonS3.deleteObjects(new DeleteObjectsRequest(BUCKET).withKeys(audio));
	}

	@Override
	public String getUrl(String audio) {
		if (!StringUtils.isEmpty(audio)) {
			return "https://s3-sa-east-1.amazonaws.com/awporta/" + audio;
		}		
		return null;
	}
	
	private ObjectMetadata enviarAudio(String novoNome, MultipartFile arquivo, AccessControlList acl) throws IOException {
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentType(arquivo.getContentType());
		metadata.setContentLength(arquivo.getSize());
		amazonS3.putObject(new PutObjectRequest(BUCKET, novoNome, arquivo.getInputStream(), metadata)
					.withAccessControlList(acl));
		return metadata;
	}

}
