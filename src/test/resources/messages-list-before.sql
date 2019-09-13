delete from message;

insert into message (id,text,tag,user_id) values
(1, 'first', 'tag1', 1),
(2, 'second', 'tag1', 1),
(3, 'third', 'TAG3', 2),
(4, 'fourth', 'tEg_4', 2);

alter sequence message_id_seq restart with 10;