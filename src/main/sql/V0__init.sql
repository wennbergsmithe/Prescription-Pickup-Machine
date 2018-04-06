create table user (
	id			bigint auto_increment not null,
	name			varchar(255),
	username		varchar(255),
	password		varchar(255),
	balanace        double,
	type			enum('client', 'employee', 'pharmacist'),
	constraint pk_user primary key (id)
);

create table prescription (
	id			bigint auto_increment not null,
	name			varchar(255),
	client_id		bigint,
	is_validated		tinyint(1) default 0,
	price			double,
	paid            tintint(1) default 0,
	constraint pk_order primary key(id)
);

