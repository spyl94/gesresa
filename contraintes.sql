--
--  1. Contraintes d'intégrité
--

--  1.1 Contraintes de validation obligatoire

select 'Ajout Contraintes de validation obligatoire ' as " " from dual;

alter table ROOMS
  modify RNAME varchar(50) not null;

alter table ROOMS
  modify ADRESS varchar(255) not null;

alter table SHOWS
  modify BAND varchar(50) not null;

alter table REPRESENTATIONS
  modify RID int not null;

alter table REPRESENTATIONS
  modify SNAME varchar(50) not null;

alter table REPRESENTATIONS
  modify RNAME varchar(50) not null;

alter table REPRESENTATIONS
  modify STARTDATE date not null;

alter table REPRESENTATIONS
  modify STARTTIME int not null;

alter table COST
  modify PRICE decimal(4,2) not null;

--  1.2 Contraintes de clé primaire

select 'Ajout Contraintes de clé primaire ' as " " from dual;

alter table ROOMS
  add constraint ROOMS_PK primary key (RNAME);

alter table SHOWS
  add constraint SHOWS_PK primary key (SNAME);

alter table CLIENTS
  add constraint CLIENTS_PK primary key (CNAME);

alter table REPRESENTATIONS
  add constraint REPRESENTATIONS_PK primary key (RID);

alter table BOOKINGCLASSES
  add constraint BOOKINGCLASSES_PK primary key (RNAME,BNAME);

alter table SEATS
  add  constraint SEATS_PK primary key (RNAME,SID);

alter table COST
  add constraint COST_PK primary key (RNAME,SNAME,BNAME);

alter table RESERVATIONS
  add  constraint RESERVATIONS_PK primary key (SID,RNAME,RID);

--  1.3 Contraintes de clé étrangère

select 'Ajout Contraintes de clé étrangère ' as " " from dual;

alter table REPRESENTATIONS
  add constraint REPRESENTATIONS_FK_SNAME foreign key (SNAME) references SHOWS(SNAME);

alter table BOOKINGCLASSES
  add constraint BOOKINGCLASSES_FK_RNAME foreign key (RNAME) references ROOMS(RNAME);

alter table SEATS
  add  constraint SEATS_FK_RNAME foreign key (RNAME) references ROOMS(RNAME);

alter table COST
  add constraint COST_FK_SNAME foreign key (SNAME) references SHOWS(SNAME);

alter table COST
  add constraint COST_FK_BOOKINGCLASSES foreign key (RNAME,BNAME) references BOOKINGCLASSES(RNAME,BNAME);

alter table RESERVATIONS
  add constraint RESERVATIONS_FK_SEAT foreign key (RNAME,SID) references SEATS(RNAME,SID);

alter table RESERVATIONS
  add  constraint RESERVATIONS_FK_REPRESENTATION foreign key (RID) references REPRESENTATIONS(RID);

alter table RESERVATIONS
  add constraint RESERVATIONS_FK_CLIENT foreign key (CNAME) references CLIENTS(CNAME);

--  1.4 Contraintes d'unicité

--  1.5 Contraintes de valeur par défaut

select 'Ajout Contraintes de valeur par défaut ' as " " from dual;

alter table ROOMS
  modify CAPACITY default (0);

--  1.5 Contraintes de domaine

select 'Ajout Contraintes de domaine ' as " " from dual;

--  Ne fonctionne pas avec la version d'Oracle:
--alter table ROOMS
  --add constraint ROOMS_CHK_ADRESS check (ADRESS LIKE '%[0-9]%');

alter table ROOMS
  add constraint ROOMS_CHK_CAPACITY check (CAPACITY >= 0);

alter table COST
  add constraint COST_CHK_PRICE check (PRICE >= 0);

alter table SHOWS
  add constraint SHOWS_CHK_LENGTH check (LENGTH > 0);

--  Ne fonctionne pas avec Oracle:
--alter table COST
  --add constraint COST_CHK_BNAME check (BNAME in (select BNAME from BOOKINGCLASSES where BNAME = COST.BNAME));

-- Solution pas très propre:
create or replace trigger COST_TRG_BNAME
  before insert or update of BNAME on COST
  for each row
  declare
  x number;
  begin
    select count(*) into x from BOOKINGCLASSES where BNAME = :NEW.BNAME and RNAME = :NEW.RNAME;
    if x <= 0
    then RAISE_APPLICATION_ERROR(-20023,'Impossible d aouter un coût cette catégorie n est pas disponible');
    end if;
  end;
/

create or replace trigger SEATS_TRG_CAPACITY
  before insert on SEATS
  for each row
  declare
  x number;
  y number;
  begin
    select count(*) into x from SEATS where RNAME = :NEW.RNAME;
    select CAPACITY into y from ROOMS where RNAME = :NEW.RNAME;
    if x + 1 > y
    then RAISE_APPLICATION_ERROR(-20023,'Impossible d aouter un siège, capacité de la salle insufisante');
    end if;
  end;
/

create or replace trigger REPRESENTATIONS_TRG_DATE
  before insert on REPRESENTATIONS
  for each row
  declare
  x number;
  begin
    select count(*) into x from REPRESENTATIONS where RNAME = :NEW.RNAME and STARTDATE = :NEW.STARTDATE and STARTTIME = :NEW.STARTTIME;
    if x > 0
    then RAISE_APPLICATION_ERROR(-20023,'Impossible de jouer deux spectacles simultanéments');
    end if;
  end;
/
--  1.5  d'intégrité référentielle

create or replace trigger SHOWS_TRG_DELETE
  after delete on SHOWS
  for each row
  begin
    delete from REPRESENTATIONS where SNAME = :OLD.SNAME and STARTDATE >= SYSDATE;
  end;
/

create or replace trigger REPRESENTATIONS_TRG_DELETE
  after delete on REPRESENTATIONS
  for each row
  begin
    delete from RESERVATIONS where RID = :OLD.RID;
  end;
/

commit;
