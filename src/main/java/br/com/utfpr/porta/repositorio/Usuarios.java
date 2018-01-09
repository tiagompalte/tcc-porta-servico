package br.com.utfpr.porta.repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.utfpr.porta.modelo.Usuario;
import br.com.utfpr.porta.repositorio.helper.usuario.UsuariosQueries;

@Repository
public interface Usuarios extends JpaRepository<Usuario, Long>, UsuariosQueries {
	
	public Optional<Usuario> findByEmail(String email);
	
	public Optional<Usuario> findByRfid(String rfid);
			
}
