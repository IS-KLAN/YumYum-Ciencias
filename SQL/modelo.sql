-- Creamos la base de datos
create database if not exists yumyum_ciencias;

-- Indicamos que usaremos la base de datos yumyum para crear las tablas
use yumyum_ciencias;

-- Borramos las siguientes tablas si ya existen en la BD
drop table if exists comida_puesto cascade;
drop table if exists evaluacion cascade;
drop table if exists comida cascade;
drop table if exists puesto cascade;
drop table if exists usuario cascade;
drop table if exists pendiente cascade;
drop table if exists administrador cascade;

-- Creación de la tabla Administrador
create table if not exists administrador (
  nombre_administrador varchar(32) primary key,
  contraseña varchar(64) not null
) engine=InnoDB default charset=utf8;

-- Creación de la tabla Pendiente
create table if not exists pendiente (
  nombre_usuario varchar(32) primary key,
  correo varchar(32) not null,
  contraseña varchar(64) not null,
  rutaImagen varchar(128),
  datos longblob,
  unique key (correo)
) engine=InnoDB default charset=utf8;

-- Creación de la tabla Usuario
create table if not exists usuario (
  nombre_usuario varchar(32) primary key,
  correo varchar(32) not null,
  contraseña varchar(64) not null,
  rutaImagen varchar(128),
  datos longblob,
  unique key (correo)
) engine=InnoDB default charset=utf8;

-- Creación de la tabla Puesto
create table if not exists puesto (
  nombre_puesto varchar(32) primary key,
  descripcion varchar(256),
  latitud varchar(64) not null,
  longitud varchar(64) not null,
  rutaImagen varchar(128),
  datos longblob,
  unique key (latitud, longitud)
) engine=InnoDB default charset=utf8;

-- Creación de la tabla Comida
create table if not exists comida (
  nombre_comida varchar(32) primary key
) engine=InnoDB default charset=utf8;

-- Creación de la tabla evaluación
create table if not exists evaluacion (
  comentario varchar(256),
  calificacion integer not null,
  nombre_puesto varchar(32) not null,
  nombre_usuario varchar(32) not null,
  primary key(nombre_puesto, nombre_usuario),
  foreign key(nombre_puesto)
  references puesto(nombre_puesto),
  foreign key(nombre_usuario)
  references usuario(nombre_usuario)
) engine=InnoDB default charset=utf8;

-- Creación de la tabla que relacionará a la comida con los puestos
create table if not exists comida_puesto (
  nombre_comida varchar(32) not null,
  nombre_puesto varchar(32) not null,
  primary key(nombre_comida, nombre_puesto),
  foreign key(nombre_comida)
  references comida(nombre_comida),
  foreign key(nombre_puesto)
  references puesto(nombre_puesto)
) engine=InnoDB default charset=utf8;

-- Se inserta el administrador por defecto.
INSERT INTO administrador (nombre_administrador, contraseña) VALUES('admin', 'passwordAdmin');

-- Se insertan comidas de prueba.
INSERT INTO comida (nombre_comida) VALUES('BOLLOS');
INSERT INTO comida (nombre_comida) VALUES('BURRITOS');
INSERT INTO comida (nombre_comida) VALUES('CREPAS');
INSERT INTO comida (nombre_comida) VALUES('CHILAQUILES');
INSERT INTO comida (nombre_comida) VALUES('DONAS');
INSERT INTO comida (nombre_comida) VALUES('ENCHILADAS');
INSERT INTO comida (nombre_comida) VALUES('FLAUTAS');
INSERT INTO comida (nombre_comida) VALUES('GORDITAS');
INSERT INTO comida (nombre_comida) VALUES('HAMBURGUESAS');
INSERT INTO comida (nombre_comida) VALUES('HELADO');
INSERT INTO comida (nombre_comida) VALUES('HOT-DOGS');
INSERT INTO comida (nombre_comida) VALUES('HUARACHES');
INSERT INTO comida (nombre_comida) VALUES('MOLLETES');
INSERT INTO comida (nombre_comida) VALUES('NACHOS');
INSERT INTO comida (nombre_comida) VALUES('PAY');
INSERT INTO comida (nombre_comida) VALUES('PIZZA');
INSERT INTO comida (nombre_comida) VALUES('PAMBAZOS');
INSERT INTO comida (nombre_comida) VALUES('QUESADILLAS');
INSERT INTO comida (nombre_comida) VALUES('SINCORNIZADAS');
INSERT INTO comida (nombre_comida) VALUES('SANDWICHES');
INSERT INTO comida (nombre_comida) VALUES('TACOS DORADOS');
INSERT INTO comida (nombre_comida) VALUES('TACOS DE GUISADO');
INSERT INTO comida (nombre_comida) VALUES('TORTAS');
INSERT INTO comida (nombre_comida) VALUES('TOSTADAS');
-- Bebidas a partir de id 20.
INSERT INTO comida (nombre_comida) VALUES('AGUA DE SABOR');
INSERT INTO comida (nombre_comida) VALUES('JUGO');
INSERT INTO comida (nombre_comida) VALUES('REFRESCO');
INSERT INTO comida (nombre_comida) VALUES('CAFÉ');
INSERT INTO comida (nombre_comida) VALUES('LICUADO');