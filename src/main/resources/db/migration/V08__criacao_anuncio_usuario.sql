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

INSERT INTO permissao values (13, 'ROLE_CADASTRAR_ANUNCIO', now(), now());
INSERT INTO permissao values (14, 'ROLE_VISUALIZAR_ANUNCIO', now(), now());

INSERT INTO grupo_permissao values (1, 13);
INSERT INTO grupo_permissao values (2, 13);
INSERT INTO grupo_permissao values (3, 14);
