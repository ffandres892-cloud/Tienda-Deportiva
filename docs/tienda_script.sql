-- =====================================================
--  BASE DE DATOS: TIENDA RUUCK - NEXUS EDITION (SQLite)
--  Script de creación de tablas y datos de ejemplo
-- =====================================================
PRAGMA foreign_keys = ON;

DROP TABLE IF EXISTS detalle_venta;
DROP TABLE IF EXISTS ventas;
DROP TABLE IF EXISTS productos;
DROP TABLE IF EXISTS categorias;
DROP TABLE IF EXISTS clientes;
DROP TABLE IF EXISTS usuarios;

-- Personas que usan el sistema (inicio de sesión)
CREATE TABLE usuarios (
    cedula        TEXT PRIMARY KEY,
    nombres       TEXT NOT NULL,
    apellidos     TEXT NOT NULL,
    nombreusuario TEXT NOT NULL UNIQUE,
    clave         TEXT NOT NULL,
    role          TEXT NOT NULL CHECK (role IN ('Administrador', 'Vendedor'))
);

-- Personas que compran
CREATE TABLE clientes (
    cedula    TEXT PRIMARY KEY,
    nombres   TEXT NOT NULL,
    apellidos TEXT NOT NULL,
    telefono  TEXT,
    correo    TEXT
);

-- Tipos de calzado
CREATE TABLE categorias (
    id     INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL UNIQUE
);

-- Zapatos que vende la tienda
CREATE TABLE productos (
    id           INTEGER PRIMARY KEY AUTOINCREMENT,
    modelo       TEXT NOT NULL UNIQUE,
    categoria_id INTEGER REFERENCES categorias(id) ON DELETE SET NULL,
    precio       REAL NOT NULL CHECK (precio > 0),
    stock        INTEGER NOT NULL DEFAULT 0 CHECK (stock >= 0)
);

-- Cabecera de cada compra
CREATE TABLE ventas (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    fecha           TEXT NOT NULL DEFAULT (date('now')),
    cliente_cedula  TEXT REFERENCES clientes(cedula) ON DELETE SET NULL,
    vendedor_cedula TEXT REFERENCES usuarios(cedula) ON DELETE SET NULL,
    total           REAL NOT NULL DEFAULT 0 CHECK (total >= 0)
);

-- Cada línea de una compra (un modelo, talla y color)
CREATE TABLE detalle_venta (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    venta_id        INTEGER NOT NULL REFERENCES ventas(id) ON DELETE CASCADE,
    producto_id     INTEGER NOT NULL REFERENCES productos(id),
    talla           TEXT NOT NULL,
    color           TEXT NOT NULL,
    cantidad        INTEGER NOT NULL CHECK (cantidad > 0),
    precio_unitario REAL NOT NULL CHECK (precio_unitario > 0)
);

-- ------------------ DATOS DE EJEMPLO ------------------
INSERT INTO usuarios VALUES
 ('1001', 'Andres', 'Herrera',  'admin',     'admin123', 'Administrador'),
 ('1002', 'Laura',  'Gomez',    'vendedor1', 'vende123', 'Vendedor'),
 ('1003', 'Mateo',  'Rojas',    'vendedor2', 'vende456', 'Vendedor');

INSERT INTO clientes VALUES
 ('2001', 'Emiliano', 'Herrera',  '3101234567', 'emiliano@correo.com'),
 ('2002', 'Sofia',    'Martinez', '3109876543', 'sofia@correo.com'),
 ('2003', 'Camilo',   'Duarte',   '3156543210', 'camilo@correo.com'),
 ('2004', 'Valentina','Ortiz',    '3207654321', 'valentina@correo.com'),
 ('2005', 'Daniel',   'Suarez',   '3001112233', 'daniel@correo.com');

INSERT INTO categorias (nombre) VALUES
 ('Deportivo'), ('Casual'), ('Bota Adventure'), ('Running Premium'), ('Urbano'), ('Atletismo');

INSERT INTO productos (modelo, categoria_id, precio, stock) VALUES
 ('RUUCK - Nexus Edition (Deportivo)',        1, 125000, 30),
 ('RUUCK - Urban Street (Casual)',            2, 145000, 25),
 ('RUUCK - Mountain Peak (Bota Adventure)',   3, 220000, 15),
 ('RUUCK - Aero Run (Running Premium)',       4, 195000, 20),
 ('RUUCK - Retro Classic (Urbano)',           5, 110000, 40),
 ('RUUCK - Track Master (Atletismo)',         6, 165000, 18);

INSERT INTO ventas (fecha, cliente_cedula, vendedor_cedula) VALUES
 ('2026-10-12', '2001', '1002'),
 ('2026-10-15', '2002', '1003'),
 ('2026-10-20', '2003', '1002');

INSERT INTO detalle_venta (venta_id, producto_id, talla, color, cantidad, precio_unitario) VALUES
 (1, 1, '40 EUR / 8 US',    'Gris Neón (Nexus Signature)', 2, 125000),
 (1, 5, '42 EUR / 9.5 US',  'Negro Absoluto',              1, 110000),
 (2, 4, '38 EUR / 6.5 US',  'Blanco Estelar',              1, 195000),
 (3, 3, '44 EUR / 11 US',   'Rojo Lava',                   1, 220000),
 (3, 6, '42 EUR / 9.5 US',  'Azul Eléctrico',              2, 165000);

-- El total de cada venta se calcula con sus líneas de detalle
UPDATE ventas SET total = (
    SELECT COALESCE(SUM(cantidad * precio_unitario), 0)
    FROM detalle_venta WHERE detalle_venta.venta_id = ventas.id
);
