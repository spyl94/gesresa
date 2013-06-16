--
--  1. DROP all tables
--

--  1.1 Relationship tables

select 'Dropping tables ' as " " from dual;

drop table COST;
drop table RESERVATIONS;

--  1.2 Entity tables

select 'Dropping tables ' as " " from dual;

drop table REPRESENTATIONS;
drop table BOOKINGCLASSES;
drop table SEATS;
drop table ROOMS;
drop table SHOWS;
drop table CLIENTS;

--
--  2. CREATE database tables
--

--  2.1 Entity tables

select 'Creating table ROOMS' as " " from dual;

create table ROOMS (
    RNAME    varchar(50),  --  name, e.g 'Opera Garnier'
    ADRESS   varchar(255), --  adress
    CAPACITY int           --  number of person
);

select 'Creating table SHOWS' as " " from dual;

create table SHOWS (
    SNAME    varchar(50),   --  name, e.g. 'Romeo et Juliette'
    LENGTH   int,           --  duration in minutes, e.g. 180 (2h30m)
    CATEGORY varchar(50),   --  kind of show, e.g. 'danse', 'music'
    BAND varchar(50)        --  name of the band, e.g. 'damos'
);

select 'Creating table CLIENTS' as " " from dual;

create table CLIENTS (
    CNAME   varchar(50)     --  name
);

select 'Creating table REPRESENTATIONS' as " " from dual;

create table REPRESENTATIONS (
    RID       int,          --  ID
    SNAME     varchar(50),  --  show NAME
    RNAME     varchar(50),  --  room NAME
    STARTDATE date,         --  start date
    STARTTIME int           --  start hour
);

select 'Creating table BOOKINGCLASSES' as " " from dual;

create table BOOKINGCLASSES (
    RNAME   varchar(50),    --  room name
    BNAME   varchar(50)     --  name
);

select 'Creating table SEATS' as " " from dual;

create table SEATS (
    RNAME  varchar(50),      --  room NAME
    BNAME  varchar(50),      --  booking class
    SID    int               --  ID
);

--  2.2 Relationship tables

--  2.2.a COST: describes the room-show-bookingclasse relationships

select 'Creating table COST' as " " from dual;

create table COST (
    RNAME  varchar(50),     -- room NAME
    SNAME  varchar(50),     -- show NAME
    BNAME  varchar(50),     -- bookingclasse name
    PRICE  decimal(4,2)     -- price (euro)
);

--  2.2.b RESERVATIONS: describes the client-seat-representation relationships

select 'Creating table RESERVATIONS' as " " from dual;

create table RESERVATIONS (
    SID   int,              --  seat ID
    RNAME  varchar(50),     -- room NAME
    RID int,                -- representation ID
    CNAME varchar(50)       --  client name
);

--
--  3. POPULATE database tables
--

--  3.1 ROOMS table

select 'Populating table ROOMS' as " " from dual;

insert into ROOMS
    values ('Le Bataclan', '50 Bd Voltaire 75011 Paris', 1000);
insert into ROOMS
    values ('La Cigale', '120 boulevard de Rochechouart, Paris 18', 1000);
insert into ROOMS
    values ('Olympia', '28, boulevard des Capucines, Paris 9', 1772);
insert into ROOMS
    values ('Palais omnisports de Paris-Bercy', '8, boulevard de Bercy, Paris 12e', 25000);

--  3.2 SHOWS table

select 'Populating table SHOWS' as " " from dual;

insert into SHOWS
    values ('Concert de Metal bien lourd', 200, 'music', 'Metallica');
insert into SHOWS
    values ('Concert des RHCP', 200, 'music', 'RHCP');
insert into SHOWS
    values ('Concert de Funk', 200, 'music', 'Jamiroquai');
insert into SHOWS
    values ('Concert Rasta', 200, 'music', 'Bob Marley');
insert into SHOWS
    values ('Concert de Pop', 200, 'music', 'bieber');

--  3.3 CLIENTS table

select 'Populating table CLIENTS' as " " from dual;

insert into CLIENTS
    values ('Lise');
insert into CLIENTS
    values ('Dupont');
insert into CLIENTS
    values ('Luc');
insert into CLIENTS
    values ('Pierre');
insert into CLIENTS
    values ('Aurel');
insert into CLIENTS
    values ('Adrien');
insert into CLIENTS
    values ('Hugo');
insert into CLIENTS
    values ('Yann');

--  3.4 REPRESENTATIONS table

select 'Populating table REPRESENTATIONS' as " " from dual;

insert into REPRESENTATIONS
    values (1, 'Concert de Metal bien lourd', 'Le Bataclan', date '2013-05-09', 12);
insert into REPRESENTATIONS
    values (2, 'Concert de Metal bien lourd', 'Palais omnisports de Paris-Bercy', date '2013-05-09', 20);
insert into REPRESENTATIONS
    values (3, 'Concert des RHCP', 'Le Bataclan',  date '2013-12-08', 16);
insert into REPRESENTATIONS
    values (4, 'Concert des RHCP', 'La Cigale', date '2013-12-09', 16);
insert into REPRESENTATIONS
    values (5, 'Concert Rasta', 'Palais omnisports de Paris-Bercy', date '2013-12-09', 16);
insert into REPRESENTATIONS
    values (6, 'Concert de Funk', 'Olympia',  date '2013-12-09', 16);
insert into REPRESENTATIONS
    values (7, 'Concert de Pop', 'Olympia', date '2013-12-09', 16);

--  3.5 BOOKINGCLASSES table

select 'Populating table BOOKINGCLASSES' as " " from dual;

insert into BOOKINGCLASSES
    values ('Le Bataclan', 'tarif unique');
insert into BOOKINGCLASSES
    values ('La Cigale', '1ere classe');
insert into BOOKINGCLASSES
    values ('La Cigale', '2nd classe');
insert into BOOKINGCLASSES
    values ('Olympia', '1ere classe');
insert into BOOKINGCLASSES
    values ('Olympia', '2nd classe');
insert into BOOKINGCLASSES
    values ('Palais omnisports de Paris-Bercy', 'Fosse');
insert into BOOKINGCLASSES
    values ('Palais omnisports de Paris-Bercy', '1ere classe');
insert into BOOKINGCLASSES
    values ('Palais omnisports de Paris-Bercy', '2nd classe');

--  3.6 SEATS table

select 'Populating table SEATS' as " " from dual;

insert into SEATS
    values ('Le Bataclan','tarif unique',1);
insert into SEATS
    values ('Le Bataclan','tarif unique',2);
insert into SEATS
    values ('Le Bataclan','tarif unique',3);
insert into SEATS
    values ('Le Bataclan','tarif unique',4);
insert into SEATS
    values ('Le Bataclan','tarif unique',5);
insert into SEATS
    values ('Le Bataclan','tarif unique',6);
insert into SEATS
    values ('Le Bataclan','tarif unique',7);
insert into SEATS
    values ('Le Bataclan','tarif unique',8);
insert into SEATS
    values ('Le Bataclan','tarif unique',9);
insert into SEATS
    values ('Le Bataclan','tarif unique',10);
insert into SEATS
    values ('Le Bataclan','tarif unique',11);
insert into SEATS
    values ('Le Bataclan','tarif unique',12);
insert into SEATS
    values ('Le Bataclan','tarif unique',13);
insert into SEATS
    values ('Le Bataclan','tarif unique',14);
insert into SEATS
    values ('Le Bataclan','tarif unique',15);
insert into SEATS
    values ('Le Bataclan','tarif unique',16);
insert into SEATS
    values ('Le Bataclan','tarif unique',17);
insert into SEATS
    values ('Le Bataclan','tarif unique',18);
insert into SEATS
    values ('Le Bataclan','tarif unique',19);
insert into SEATS
    values ('Le Bataclan','tarif unique',20);
insert into SEATS
    values ('La Cigale','1ere classe',1);
insert into SEATS
    values ('La Cigale','1ere classe',2);
insert into SEATS
    values ('La Cigale','1ere classe',3);
insert into SEATS
    values ('La Cigale','1ere classe',4);
insert into SEATS
    values ('La Cigale','2nd classe',5);
insert into SEATS
    values ('La Cigale','2nd classe',6);
insert into SEATS
    values ('La Cigale','2nd classe',7);
insert into SEATS
    values ('La Cigale','2nd classe',8);
insert into SEATS
    values ('Olympia','1ere classe',1);
insert into SEATS
    values ('Olympia','1ere classe',2);
insert into SEATS
    values ('Olympia','1ere classe',3);
insert into SEATS
    values ('Olympia','1ere classe',4);
insert into SEATS
    values ('Olympia','2nd classe',5);
insert into SEATS
    values ('Olympia','2nd classe',6);
insert into SEATS
    values ('Olympia','2nd classe',7);
insert into SEATS
    values ('Olympia','2nd classe',8);
insert into SEATS
    values ('Palais omnisports de Paris-Bercy','Fosse',1);
insert into SEATS
    values ('Palais omnisports de Paris-Bercy','Fosse',2);
insert into SEATS
    values ('Palais omnisports de Paris-Bercy','Fosse',3);
insert into SEATS
    values ('Palais omnisports de Paris-Bercy','Fosse',4);
insert into SEATS
    values ('Palais omnisports de Paris-Bercy','1ere classe',5);
insert into SEATS
    values ('Palais omnisports de Paris-Bercy','1ere classe',6);
insert into SEATS
    values ('Palais omnisports de Paris-Bercy','1ere classe',7);
insert into SEATS
    values ('Palais omnisports de Paris-Bercy','1ere classe',8);
insert into SEATS
    values ('Palais omnisports de Paris-Bercy','2nd classe',9);
insert into SEATS
    values ('Palais omnisports de Paris-Bercy','2nd classe',10);
insert into SEATS
    values ('Palais omnisports de Paris-Bercy','2nd classe',11);
insert into SEATS
    values ('Palais omnisports de Paris-Bercy','2nd classe',12);
insert into SEATS
    values ('Palais omnisports de Paris-Bercy','2nd classe',13);
insert into SEATS
    values ('Palais omnisports de Paris-Bercy','2nd classe',14);


--  3.7 COST table

select 'Populating table COST' as " " from dual;

insert into COST
    values ('Le Bataclan','Concert de Metal bien lourd','tarif unique', 20);
insert into COST
    values ('Le Bataclan','Concert des RHCP','tarif unique', 20);
insert into COST
    values ('La Cigale','Concert des RHCP','1ere classe', 20);
insert into COST
    values ('La Cigale','Concert des RHCP','2nd classe', 20);
insert into COST
    values ('Olympia','Concert de Funk','1ere classe', 50);
insert into COST
    values ('Olympia','Concert de Funk','2nd classe', 20);
insert into COST
    values ('Olympia','Concert de Pop','1ere classe', 50);
insert into COST
    values ('Olympia','Concert de Pop','2nd classe', 20);
insert into COST
    values ('Palais omnisports de Paris-Bercy','Concert de Metal bien lourd','Fosse', 20);
insert into COST
    values ('Palais omnisports de Paris-Bercy','Concert de Metal bien lourd','1ere classe', 50);
insert into COST
    values ('Palais omnisports de Paris-Bercy','Concert de Metal bien lourd','2nd classe', 30);
insert into COST
    values ('Palais omnisports de Paris-Bercy','Concert Rasta','Fosse', 10);
insert into COST
    values ('Palais omnisports de Paris-Bercy','Concert Rasta','1ere classe', 30);
insert into COST
    values ('Palais omnisports de Paris-Bercy','Concert Rasta','2nd classe', 20);

--insert into RESERVATIONS
  --  values (1, 'Le Bataclan', 1,'Luc');

commit;
