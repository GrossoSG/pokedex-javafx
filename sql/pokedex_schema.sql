PRAGMA foreign_keys = ON;

CREATE TABLE pokemon (
    numero INTEGER PRIMARY KEY,
    nome TEXT NOT NULL UNIQUE,
    tipo_1 TEXT NOT NULL,
    tipo_2 TEXT,
    hp INTEGER NOT NULL,
    atk INTEGER NOT NULL,
    def INTEGER NOT NULL,
    spc_atk INTEGER NOT NULL,
    spc_def INTEGER NOT NULL,
    speed INTEGER NOT NULL,
    total INTEGER NOT NULL,
    altura_m REAL NOT NULL,
    peso_kg REAL NOT NULL,
    categoria TEXT,
    habilidade_1 TEXT NOT NULL,
    habilidade_2 TEXT,
    habilidade_hidden TEXT,
    estagio_evolutivo INTEGER NOT NULL,
    evolui_de TEXT,
    evolui_para TEXT,
    pseudo_lendario INTEGER NOT NULL CHECK (pseudo_lendario IN (0, 1)),
    lendario INTEGER NOT NULL CHECK (lendario IN (0, 1)),
    mitico INTEGER NOT NULL CHECK (mitico IN (0, 1)),
    genderless INTEGER NOT NULL DEFAULT 0 CHECK (genderless IN (0, 1)),
    descricao TEXT
);

CREATE TABLE pokemon_defesa (
    numero_pokemon INTEGER NOT NULL,
    tipo TEXT NOT NULL,
    relacao TEXT NOT NULL CHECK (relacao IN ('fraqueza', 'imunidade', 'resistencia')),
    PRIMARY KEY (numero_pokemon, tipo),
    FOREIGN KEY (numero_pokemon) REFERENCES pokemon(numero)
);

CREATE INDEX idx_pokemon_nome ON pokemon(nome);
CREATE INDEX idx_defesa_tipo_relacao ON pokemon_defesa(tipo, relacao);
