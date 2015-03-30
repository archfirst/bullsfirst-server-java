-- -----------------------------------------------------------------------------
-- Caution: Do not split statements on multiple lines - Seam cannot parse them.
-- Backslash as a line continuation character doesn't work either.
-- -----------------------------------------------------------------------------

set autocommit=0;

-- -----------------------------------------------------------------------------
-- Parties
-- -----------------------------------------------------------------------------
insert into Party (id, version) values (1, 0);
insert into Party (id, version) values (2, 0);
insert into Party (id, version) values (3, 0);

insert into Person (id, firstName, lastName) values (1, 'John', 'Horner');
insert into Person (id, firstName, lastName) values (2, 'Karen', 'Horner');
insert into Person (id, firstName, lastName) values (3, 'Jack', 'Horner');

-- -----------------------------------------------------------------------------
-- Users
-- -----------------------------------------------------------------------------
insert into Users (id, version, username, passwordHash, person_id) values (1, 0, 'jhorner', 'sfT5pSPjb9lp9Fc+Ja9FQA==', 1);
insert into Users (id, version, username, passwordHash, person_id) values (2, 0, 'khorner', 'sfT5pSPjb9lp9Fc+Ja9FQA==', 2);

-- -----------------------------------------------------------------------------
-- UserGroup
-- -----------------------------------------------------------------------------
insert into UserGroup (id, version, username, groupname) values (1, 0, 'jhorner', 'user');
insert into UserGroup (id, version, username, groupname) values (2, 0, 'khorner', 'user');

commit;