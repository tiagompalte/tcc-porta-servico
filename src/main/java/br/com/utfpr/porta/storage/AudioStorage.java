package br.com.utfpr.porta.storage;

import org.springframework.web.multipart.MultipartFile;

public interface AudioStorage {
	
	public void salvar(String name, MultipartFile file);

	public byte[] recuperar(String audio);
	
	public void excluir(String audio);
	
	public String getUrl(String audio);

}
