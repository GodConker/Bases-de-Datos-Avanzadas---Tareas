CREATE DATABASE IF NOT EXISTS BDClase280524;

USE BDClase280524;

CREATE TABLE alumnos (
idAlumno int not null auto_increment,
nombres varchar(30) not null,
apellidoPaterno varchar(20) not null,
apellidoMaterno varchar(20) default null,
eliminado bit(1) not null default b'1',
activo bit(1) not null default b'0',
PRIMARY KEY (idAlumno)
);

/*INSERT INTO alumnos (nombres, apellidoPaterno, apellidoMaterno, eliminado, activo) VALUES
('Juan', 'Pérez', 'López', b'0', b'1'),
('María', 'González', 'Martínez', b'0', b'1'),
('Carlos', 'Sánchez', 'Rodríguez', b'0', b'0'),
('Ana', 'Martín', 'Hernández', b'1', b'0'),
('Luis', 'Ruiz', 'García', b'0', b'1'),
('Elena', 'Díaz', 'Morales', b'1', b'0');

SELECT * FROM alumnos; */