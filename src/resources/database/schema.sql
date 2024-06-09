CREATE TABLE resultados (
    id_resultado INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre_partida VARCHAR(30) NOT NULL,
    nombre_jugador1 VARCHAR(40) NOT NULL,
    nombre_jugador2 VARCHAR(40) NOT NULL,
    ganador VARCHAR(40) NOT NULL,
    punto INTEGER NOT NULL,
    estado VARCHAR(10) NOT NULL
);
