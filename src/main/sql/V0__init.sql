create table user (
	id			bigint auto_increment not null,
	name			varchar(255),
	username		varchar(255),
	password		varchar(255),
	balance         double,
	type			enum('client', 'employee', 'pharmacist'),
	isFrozen	BOOL DEFAULT FALSE,
	salt			varchagur(255),
	allergies       varchar(255),
	constraint pk_user primary key (id)
);

create table prescription (
	id			bigint auto_increment not null,
	name			varchar(255),
	client_id		bigint,
	is_validated		tinyint(1) default 0,
	price			double,
	paid            tinyint(1) default 0,
	warnings        varchar(255),
	easy_open       BOOL DEFAULT FALSE,
	constraint pk_order primary key(id)
);


