CREATE TABLE token_reset_senha (
    codigo_usuario BIGINT(20) NOT NULL,
    token VARCHAR(80) NOT NULL,
    data_hora_criacao DATETIME NOT NULL,
    data_hora_alteracao DATETIME NOT NULL,
    CONSTRAINT PK_token_reset_senha PRIMARY KEY (codigo_usuario),
    CONSTRAINT FK_token_reset_senha_usuario FOREIGN KEY (codigo_usuario) REFERENCES usuario(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;