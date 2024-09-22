CREATE DATABASE IF NOT EXISTS vulpix_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
  
USE vulpix_db;

CREATE TABLE IF NOT EXISTS usuario (
    id_usuario VARCHAR(36) NOT NULL,
    nome VARCHAR(255) NOT NULL,
    sobrenome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    status TINYINT(1) NOT NULL DEFAULT 1,
    telefone VARCHAR(20),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id_usuario)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS empresa (
    id_empresa VARCHAR(36) NOT NULL,
    razao_social VARCHAR(255) NOT NULL,
    nome_fantasia VARCHAR(255) NOT NULL,
    cnpj VARCHAR(20) NOT NULL UNIQUE,
    cep VARCHAR(20),
    logradouro VARCHAR(255),
    numero VARCHAR(20),
    bairro VARCHAR(255),
    complemento VARCHAR(255),
    cidade VARCHAR(100),
    estado VARCHAR(100),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    responsavel VARCHAR(36) NOT NULL,
    PRIMARY KEY (id_empresa),
    CONSTRAINT fk_empresa_responsavel
        FOREIGN KEY (responsavel) REFERENCES usuario(id_usuario)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS integracao (
    id_integracao VARCHAR(36) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    ig_user_id VARCHAR(255),
    client_id VARCHAR(255),
    client_secret VARCHAR(255),
    access_token VARCHAR(255),
    access_token_expire_date DATETIME,
    status TINYINT(1),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    fk_empresa VARCHAR(36) NOT NULL,
    PRIMARY KEY (id_integracao),
    CONSTRAINT fk_integracao_empresa
        FOREIGN KEY (fk_empresa) REFERENCES empresa(id_empresa)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS config_prompt (
    id_config_prompt VARCHAR(36) NOT NULL,
    chave VARCHAR(255) NOT NULL,
    valor TEXT NOT NULL,
    fk_empresa VARCHAR(36) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id_config_prompt),
    INDEX idx_config_prompt_fk_empresa (fk_empresa),
    CONSTRAINT fk_config_prompt_empresa
        FOREIGN KEY (fk_empresa) REFERENCES empresa(id_empresa)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS publicacao (
    id_publicacao VARCHAR(36) NOT NULL,
    legenda VARCHAR(255),
    tipo VARCHAR(50),
    image_url VARCHAR(2048),
    data_agendamento DATETIME,
    total_like INT DEFAULT 0,
    plataforma VARCHAR(100),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    id_returned VARCHAR(255),
    PRIMARY KEY (id_publicacao)
) ENGINE=InnoDB;