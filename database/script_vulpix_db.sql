-- Criação do banco de dados
CREATE DATABASE IF NOT EXISTS vulpix_db
    WITH ENCODING 'UTF8';

-- Conexão com o banco de dados
\c vulpix_db;

-- Criação da tabela usuario
CREATE TABLE IF NOT EXISTS usuario (
    id_usuario UUID NOT NULL DEFAULT gen_random_uuid(),
    nome VARCHAR(255) NOT NULL,
    sobrenome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    estado VARCHAR(50) NOT NULL DEFAULT 'AGUARDANDO_PAGAMENTO',
    telefone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_usuario)
);

-- Criação da tabela empresa
CREATE TABLE IF NOT EXISTS empresa (
    id_empresa UUID NOT NULL DEFAULT gen_random_uuid(),
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
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    responsavel UUID NOT NULL,
    PRIMARY KEY (id_empresa),
    CONSTRAINT fk_empresa_responsavel
        FOREIGN KEY (responsavel) REFERENCES usuario(id_usuario)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

-- Criação da tabela integracao
CREATE TABLE IF NOT EXISTS integracao (
    id_integracao UUID NOT NULL DEFAULT gen_random_uuid(),
    tipo VARCHAR(50) NOT NULL,
    ig_user_id VARCHAR(255),
    client_id VARCHAR(255),
    client_secret VARCHAR(255),
    access_token VARCHAR(255),
    access_token_expire_date TIMESTAMP,
    status BOOLEAN,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fk_empresa UUID NOT NULL,
    PRIMARY KEY (id_integracao),
    CONSTRAINT fk_integracao_empresa
        FOREIGN KEY (fk_empresa) REFERENCES empresa(id_empresa)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

-- Criação da tabela config_prompt
CREATE TABLE IF NOT EXISTS config_prompt (
    id_config_prompt UUID NOT NULL DEFAULT gen_random_uuid(),
    chave VARCHAR(255) NOT NULL,
    valor TEXT NOT NULL,
    fk_empresa UUID NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_config_prompt),
    INDEX idx_config_prompt_fk_empresa (fk_empresa),
    CONSTRAINT fk_config_prompt_empresa
        FOREIGN KEY (fk_empresa) REFERENCES empresa(id_empresa)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

-- Criação da tabela publicacao
CREATE TABLE IF NOT EXISTS publicacao (
    id_publicacao UUID NOT NULL DEFAULT gen_random_uuid(),
    legenda VARCHAR(255),
    tipo VARCHAR(50),
    image_url VARCHAR(2048),
    data_agendamento TIMESTAMP,
    total_like INT DEFAULT 0,
    plataforma VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    id_returned VARCHAR(255),
    PRIMARY KEY (id_publicacao)
);