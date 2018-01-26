
INSERT INTO permissao values (11, 'ROLE_VISUALIZAR_AUTORIZACAO', now(), now());
INSERT INTO permissao values (12, 'ROLE_VISUALIZAR_LOG', now(), now());

INSERT INTO grupo_permissao values (3, 11);

INSERT INTO grupo_permissao values (1, 12);
INSERT INTO grupo_permissao values (2, 12);