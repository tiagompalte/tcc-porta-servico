package br.com.utfpr.porta.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.utfpr.porta.modelo.Endereco;
import br.com.utfpr.porta.repositorio.helper.endereco.EnderecoQueries;

@Repository
public interface Enderecos extends JpaRepository<Endereco, Long>, EnderecoQueries {
	
	public Endereco findByCepAndNumero(String cep, String numero);

}
