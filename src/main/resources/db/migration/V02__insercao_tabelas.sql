INSERT INTO grupo VALUES (1, 'Suporte', now(), now());
INSERT INTO grupo VALUES (2, 'Anfitrião', now(), now());
INSERT INTO grupo VALUES (3, 'Usuário', now(), now());

INSERT INTO permissao VALUES (1, 'ROLE_EDITAR_TODOS_USUARIOS', now(), now());
INSERT INTO permissao VALUES (2, 'ROLE_EDITAR_TODOS_ESTABELECIMENTOS', now(), now());
INSERT INTO permissao VALUES (3, 'ROLE_CADASTRAR_AUTORIZACAO', now(), now());
INSERT INTO permissao VALUES (4, 'ROLE_CADASTRAR_PORTA', now(), now());
INSERT INTO permissao VALUES (5, 'ROLE_CADASTRAR_ANUNCIO', now(), now());
INSERT INTO permissao VALUES (6, 'ROLE_VISUALIZAR_ANUNCIO', now(), now());
INSERT INTO permissao VALUES (7, 'ROLE_EDITAR_PROPRIO_USUARIO', now(), now());
INSERT INTO permissao VALUES (8, 'ROLE_EDITAR_PROPRIO_ESTABELECIMENTO', now(), now());
INSERT INTO permissao VALUES (9, 'ROLE_VISUALIZAR_USUARIO', now(), now());

INSERT INTO grupo_permissao VALUES (1, 1);
INSERT INTO grupo_permissao VALUES (1, 2);
INSERT INTO grupo_permissao VALUES (1, 3);
INSERT INTO grupo_permissao VALUES (1, 4);
INSERT INTO grupo_permissao VALUES (1, 5);
INSERT INTO grupo_permissao VALUES (1, 6);
INSERT INTO grupo_permissao VALUES (1, 9);
INSERT INTO grupo_permissao VALUES (2, 3);
INSERT INTO grupo_permissao VALUES (2, 4);
INSERT INTO grupo_permissao VALUES (2, 5);
INSERT INTO grupo_permissao VALUES (2, 6);
INSERT INTO grupo_permissao VALUES (2, 8);
INSERT INTO grupo_permissao VALUES (2, 9);
INSERT INTO grupo_permissao VALUES (3, 6);
INSERT INTO grupo_permissao VALUES (3, 7);
INSERT INTO grupo_permissao VALUES (3, 9);

INSERT INTO endereco(logradouro, numero, bairro, cep, cidade, estado, data_hora_criacao, data_hora_alteracao) VALUES ('Av. Sete de Setembro', '3165', 'Rebouças', '80.230-901', 'Curitiba', 'PR', now(), now());

INSERT INTO pessoa(nome, tipo_pessoa, cpf_cnpj, data_hora_criacao, data_hora_alteracao) VALUES ('Administrador', 'FISICA', '18294700002', now(), now());

INSERT INTO usuario(codigo_pessoa, email, senha_site, data_hora_criacao, data_hora_alteracao) VALUES ((SELECT codigo FROM pessoa WHERE cpf_cnpj = '18294700002'), 'admin', '$2a$06$m80TFwmzpVpp9szafs9MtukurC3vdvhMWuAm8e.9l/37.28F0pwoK', now(), now());

INSERT INTO estabelecimento(nome, codigo_endereco, codigo_responsavel, data_hora_criacao, data_hora_alteracao) VALUES ('Sistema', (SELECT codigo FROM endereco WHERE cep = '80.230-901' and numero = '3165'), (SELECT codigo FROM usuario WHERE email = 'admin'), now(), now());

INSERT INTO usuario_grupo VALUES ((SELECT codigo FROM usuario WHERE email = 'admin'), 1);