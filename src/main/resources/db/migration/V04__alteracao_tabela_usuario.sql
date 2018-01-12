alter table usuario add column nr_tentativa_acesso_porta integer not null default 0;
alter table usuario add column nr_tentativa_acesso_site integer not null default 0;