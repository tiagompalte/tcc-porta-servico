CREATE TABLE parametro (
    codigo VARCHAR(50),
	descricao VARCHAR(200),
    valor VARCHAR(100) NOT NULL,
    data_hora_criacao DATETIME NOT NULL,
    data_hora_alteracao DATETIME NOT NULL,
    CONSTRAINT PK_parametro PRIMARY KEY (codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO permissao VALUES (10, 'ROLE_CADASTRAR_PARAMETRO', now(), now());

INSERT INTO grupo_permissao VALUES (1, 10);