alter table usuario add column codigo_estabelecimento BIGINT(20);
alter table usuario add constraint FK_estabelecimento foreign key (codigo_estabelecimento) references estabelecimento(codigo);

update usuario u set codigo_estabelecimento = (select codigo from estabelecimento where codigo_responsavel = u.codigo);