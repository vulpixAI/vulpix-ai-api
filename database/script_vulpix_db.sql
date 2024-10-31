-- Criação do banco de dados
CREATE DATABASE IF NOT EXISTS vulpix_db
    WITH ENCODING 'UTF8';

-- Criação da tabela Usuario
CREATE TABLE usuario (
    id_usuario UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(255),
    sobrenome VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    senha VARCHAR(255),
    status VARCHAR(50) NOT NULL DEFAULT 'AGUARDANDO_PAGAMENTO',
    telefone VARCHAR(15),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Criação da tabela Empresa
CREATE TABLE empresa (
    id_empresa UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    razao_social VARCHAR(255),
    nome_fantasia VARCHAR(255),
    cnpj VARCHAR(14) UNIQUE,
    cep VARCHAR(10),
    logradouro VARCHAR(255),
    numero VARCHAR(10),
    bairro VARCHAR(255),
    complemento VARCHAR(255),
    cidade VARCHAR(255),
    estado VARCHAR(2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    responsavel UUID REFERENCES usuario(id_usuario) ON DELETE SET NULL
);

-- Criação da tabela Plano
CREATE TABLE plano (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255),
    preco DECIMAL(10, 2)
);

-- Criação da tabela Integracao
CREATE TABLE integracao (
    id_integracao UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tipo VARCHAR(50),
    ig_user_id VARCHAR(255),
    client_id VARCHAR(255),
    client_secret VARCHAR(255),
    access_token VARCHAR(255),
    access_token_expire_date TIMESTAMP,
    status BOOLEAN,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fk_empresa UUID REFERENCES empresa(id_empresa) ON DELETE CASCADE
);

-- Criação da tabela Publicacao
CREATE TABLE publicacao (
    id_publicacao UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    legenda TEXT,
    tipo VARCHAR(50),
    image_url VARCHAR(2048),
    data_agendamento TIMESTAMP,
    total_like INTEGER,
    plataforma VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    id_returned VARCHAR(255),
    fk_empresa UUID REFERENCES empresa(id_empresa) ON DELETE CASCADE
);

-- Criação da tabela ConfigPrompt
CREATE TABLE config_prompt (
    id_config_prompt UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    form JSON,
    fk_empresa UUID REFERENCES empresa(id_empresa) ON DELETE CASCADE
);
