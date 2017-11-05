package br.com.utfpr.porta.storage;

import org.springframework.web.multipart.MultipartFile;

public class AudioStorageRunnable implements Runnable {

	private MultipartFile file;
	private String name;
	private AudioStorage audioStorage;
	
	public AudioStorageRunnable(MultipartFile file, String name, AudioStorage audioStorage) {
		this.file = file;
		this.name = name;
		this.audioStorage = audioStorage;
	}

	@Override
	public void run() {
		this.audioStorage.salvar(name, file);
	}	

}
