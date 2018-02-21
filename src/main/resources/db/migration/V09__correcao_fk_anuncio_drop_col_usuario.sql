ALTER TABLE anuncio DROP FOREIGN KEY FK_anuncio_estabelecimento;
ALTER TABLE anuncio ADD CONSTRAINT FK_anuncio_estabelecimento FOREIGN KEY (codigo_estabelecimento) REFERENCES estabelecimento(codigo);

ALTER TABLE usuario DROP COLUMN nr_tentativa_acesso_porta;
ALTER TABLE usuario DROP COLUMN nr_tentativa_acesso_site;