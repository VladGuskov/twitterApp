delete from user_role;
delete from usr;

insert into usr(id,username, password, active) values
(1,'qq', '$2a$08$/aboYPjJq/vStY7BRLIRPOizEi3SpOp7KnXSU4jYuKUMOiSmQXC9C', true),
(2,'tt', '$2a$08$H1qRs3Ix/IHFOFZ.Q7VU1evPQh2ZRydMVweoAKZUhOXiFvVCn6SvG', true);

insert into user_role(user_id, roles) values
(1, 'ADMIN'), (1, 'USER'),
(2, 'USER');