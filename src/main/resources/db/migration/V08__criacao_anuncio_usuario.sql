CREATE TABLE anuncio_usuario (
    codigo_usuario BIGINT(20) NOT NULL,
    codigo_anuncio BIGINT(20) NOT NULL,
    data_hora_criacao DATETIME NOT NULL,
    data_hora_alteracao DATETIME NOT NULL,
    CONSTRAINT PK_anuncio_usuario PRIMARY KEY (codigo_usuario, codigo_anuncio),
    CONSTRAINT FK_anuncio_usuario_usuario FOREIGN KEY (codigo_usuario) REFERENCES usuario(codigo),
    CONSTRAINT FK_anuncio_usuario_anuncio FOREIGN KEY (codigo_anuncio) REFERENCES anuncio(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE anuncio ADD COLUMN data_expiracao DATE NOT NULL;
ALTER TABLE anuncio ADD COLUMN data_publicacao DATE NOT NULL;
ALTER TABLE anuncio ADD COLUMN descricao_resumida VARCHAR(50) NOT NULL;
ALTER TABLE anuncio ADD COLUMN preco DECIMAL(10,2) NOT NULL;

DELETE FROM grupo_permissao WHERE codigo_grupo = 1 AND codigo_permissao = 6;
DELETE FROM grupo_permissao WHERE codigo_grupo = 2 AND codigo_permissao = 6;