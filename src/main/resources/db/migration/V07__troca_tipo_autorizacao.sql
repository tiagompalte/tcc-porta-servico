
ALTER TABLE autorizacao ADD COLUMN dia_mes INT(2);

UPDATE autorizacao SET tipo_autorizacao = 'SEMANAL' where tipo_autorizacao == 'PROGRAMADO';