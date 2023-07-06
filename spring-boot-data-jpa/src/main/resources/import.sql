/* Populate table clientes */
INSERT INTO clientes (nombre, apellido, email, create_at, foto) VALUES('Miguel', 'Ohara', 'Miguel@correo.com', '2020-06-25', '');
INSERT INTO clientes (nombre, apellido, email, create_at, foto) VALUES('Andres', 'Guzman', 'Andres@correo.com', '2021-07-20', '');
INSERT INTO clientes (nombre, apellido, email, create_at, foto) VALUES('Chise', 'Hatori', 'Chise@correo.com', '2018-05-14', '');
INSERT INTO clientes (nombre, apellido, email, create_at, foto) VALUES('Jorge', 'Gutierrez', 'Jorge@correo.com', '2021-10-21', '');
INSERT INTO clientes (nombre, apellido, email, create_at, foto) VALUES('Tiburcio', 'Lopez', 'Tiburcio@correo.com', '2021-12-17', '');
INSERT INTO clientes (nombre, apellido, email, create_at, foto) VALUES('Ivan', 'Valle', 'Ivan@correo.com', '2019-09-04', '');
INSERT INTO clientes (nombre, apellido, email, create_at, foto) VALUES('Manuel', 'Lagos', 'Manuel@correo.com', '2019-07-20', '');
INSERT INTO clientes (nombre, apellido, email, create_at, foto) VALUES('Luigi', 'Rodriguez', 'Luigi@correo.com', '2022-03-13', '');
INSERT INTO clientes (nombre, apellido, email, create_at, foto) VALUES('Joji', 'Matheis', 'Joji@correo.com', '2021-02-20', '');
INSERT INTO clientes (nombre, apellido, email, create_at, foto) VALUES('Clarence', 'Gomez', 'Clarence@correo.com', '2022-05-04', '');
INSERT INTO clientes (nombre, apellido, email, create_at, foto) VALUES('Miles', 'Silva', 'Miles@correo.com', '2023-01-04', '');
INSERT INTO clientes (nombre, apellido, email, create_at, foto) VALUES('Robyn', 'Fernandez', 'Robyn@correo.com', '2020-01-17', '');
INSERT INTO clientes (nombre, apellido, email, create_at, foto) VALUES('Ted', 'Garcia', 'Ted@correo.com', '2019-03-19', '');
INSERT INTO clientes (nombre, apellido, email, create_at, foto) VALUES('Andrew', 'Leyva', 'Andrew@correo.com', '2018-10-28', '');
INSERT INTO clientes (nombre, apellido, email, create_at, foto) VALUES('Jonah', 'Rodriguez', 'Jonah@correo.com', '2020-11-21', '');
INSERT INTO clientes (nombre, apellido, email, create_at, foto) VALUES('Idah', 'Romero', 'Idah@correo.com', '2021-04-14', '');
INSERT INTO clientes (nombre, apellido, email, create_at, foto) VALUES('George', 'Diaz', 'George@correo.com', '2022-01-15', '');
INSERT INTO clientes (nombre, apellido, email, create_at, foto) VALUES('Isaac', 'Ruiz', 'Isaac@correo.com', '2021-06-19', '');
INSERT INTO clientes (nombre, apellido, email, create_at, foto) VALUES('Andre', 'Sanchez', 'Andre@correo.com', '2020-09-24', '');
INSERT INTO clientes (nombre, apellido, email, create_at, foto) VALUES('Michael', 'Soto', 'Michael@correo.com', '2023-07-25', '');
INSERT INTO clientes (nombre, apellido, email, create_at, foto) VALUES('Clark', 'Sepulveda', 'Clark@correo.com', '2023-05-27', '');
INSERT INTO clientes (nombre, apellido, email, create_at, foto) VALUES('Alfonso', 'Araya', 'Alfonso@correo.com', '2023-06-06', '');
INSERT INTO clientes (nombre, apellido, email, create_at, foto) VALUES('Joshua', 'Espinoza', 'Joshua@correo.com', '2019-06-09', '');
INSERT INTO clientes (nombre, apellido, email, create_at, foto) VALUES('Ilse', 'Contreras', 'Ilse@correo.com', '2022-08-12', '');
INSERT INTO clientes (nombre, apellido, email, create_at, foto) VALUES('Vladimir', 'Pavlov', 'Vladimir@correo.com', '2020-06-25', '');

/* Populate table productos */
INSERT INTO productos (nombre, precio, create_at) VALUES('Panasonic Pantalla LCD', 259990, NOW());
INSERT INTO productos (nombre, precio, create_at) VALUES('Sony Camara digital DSC-W320B', 123490, NOW());
INSERT INTO productos (nombre, precio, create_at) VALUES('Apple iPod shuffle', 1499990, NOW());
INSERT INTO productos (nombre, precio, create_at) VALUES('Sony Notebook Z110', 37990, NOW());
INSERT INTO productos (nombre, precio, create_at) VALUES('Hewlett Packard Multifuncional F2280', 69990, NOW());
INSERT INTO productos (nombre, precio, create_at) VALUES('Bianchi Bicicleta Aro 26', 69990, NOW());
INSERT INTO productos (nombre, precio, create_at) VALUES('Mica Comoda 5 Cajones', 299990, NOW());

/* Creamos algunas facturas */
INSERT INTO facturas (descripcion, observacion, cliente_id, create_at) VALUES('Factura equipos de oficina', null, 1, NOW());
INSERT INTO facturas_items (cantidad, factura_id, producto_id) VALUES(1, 1, 1);
INSERT INTO facturas_items (cantidad, factura_id, producto_id) VALUES(2, 1, 4);
INSERT INTO facturas_items (cantidad, factura_id, producto_id) VALUES(1, 1, 5);
INSERT INTO facturas_items (cantidad, factura_id, producto_id) VALUES(1, 1, 7);

INSERT INTO facturas (descripcion, observacion, cliente_id, create_at) VALUES('Factura bicicleta', 'Compra de tres bicicletas', 1, NOW());
INSERT INTO facturas_items (cantidad, factura_id, producto_id) VALUES(3, 2, 6);