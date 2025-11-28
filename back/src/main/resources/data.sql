MERGE INTO usuario (id, nome, email, senha, perfil) KEY(id) VALUES
    (1, 'Administrador do Sistema', 'admin@greenlog.com', 'admin123', 'ADMIN'),
    (2, 'Usuário Padrão', 'user@greenlog.com', 'user123', 'USER');

MERGE INTO bairro (id, nome, descricao) KEY(id) VALUES
    (1, 'Centro', 'Região central da cidade'),
    (2, 'Jardim das Flores', 'Bairro residencial'),
    (3, 'Vila Nova', 'Área com comércio variado'),
    (4, 'Industrial', 'Distrito industrial'),
    (5, 'Lago Azul', 'Bairro de zona mista');

MERGE INTO tipo_residuo (id, nome) KEY(id) VALUES
    (1, 'Orgânico'),
    (2, 'Plástico'),
    (3, 'Metal'),
    (4, 'Vidro'),
    (5, 'Papel'),
    (6, 'Eletrônico');

MERGE INTO caminhao (id, placa, motorista, capacidade_kg) KEY(id) VALUES
    (1, 'ABC1D23', 'João Silva', 5000),
    (2, 'XYZ9A88', 'Carlos Moura', 3500),
    (3, 'QWE4F55', 'Ana Pereira', 4500);

MERGE INTO caminhao_residuo (caminhao_id, tipo_residuo_id) KEY(caminhao_id, tipo_residuo_id) VALUES
    (1, 1), (1, 2), (1, 3),
    (2, 2), (2, 5),
    (3, 1), (3, 4), (3, 6);

MERGE INTO ponto_coleta (id, nome_responsavel, contato, endereco, bairro_id) KEY(id) VALUES
    (1, 'Marcos Almeida', '(11)99999-1111', 'Rua A, 123', 1),
    (2, 'Fernanda Rocha', '(11)98888-2222', 'Av. B, 456', 2),
    (3, 'Cláudia Lopes', '(11)97777-3333', 'Rua C, 789', 3),
    (4, 'Ricardo Santos', '(11)96666-4444', 'Av. D, 321', 4);

MERGE INTO ponto_residuo (ponto_coleta_id, tipo_residuo_id) KEY(ponto_coleta_id, tipo_residuo_id) VALUES
    (1, 1), (1, 2), (1, 5),
    (2, 2), (2, 3),
    (3, 4), (3, 6),
    (4, 1), (4, 2), (4, 3), (4, 4);

MERGE INTO rota (id, nome) KEY(id) VALUES
    (1, 'Rota Norte'),
    (2, 'Rota Leste'),
    (3, 'Rota Sul');

MERGE INTO rota_bairro (rota_id, bairro_id) KEY(rota_id, bairro_id) VALUES
    (1, 1), (1, 2),
    (2, 3), (2, 5),
    (3, 4), (3, 1);

MERGE INTO conexao_bairro (id, bairro_origem_id, bairro_destino_id, distancia) KEY(id) VALUES
    (1, 1, 2, 3.5),
    (2, 2, 3, 2.1),
    (3, 3, 4, 4.0),
    (4, 4, 5, 6.8),
    (5, 5, 1, 5.9);

MERGE INTO itinerario (id, data, caminhao_id, rota_id) KEY(id) VALUES
    (1, DATE '2025-03-01', 1, 1),
    (2, DATE '2025-03-01', 2, 2),
    (3, DATE '2025-03-02', 3, 3),
    (4, DATE '2025-03-03', 1, 3);

ALTER TABLE USUARIO ALTER COLUMN id RESTART WITH (
    SELECT COALESCE(MAX(id), 0) + 1 FROM USUARIO 
);

ALTER TABLE bairro ALTER COLUMN id RESTART WITH (
    SELECT COALESCE(MAX(id), 0) + 1 FROM bairro
);

ALTER TABLE tipo_residuo ALTER COLUMN id RESTART WITH (
    SELECT COALESCE(MAX(id), 0) + 1 FROM tipo_residuo
);

ALTER TABLE caminhao ALTER COLUMN id RESTART WITH (
    SELECT COALESCE(MAX(id), 0) + 1 FROM caminhao
);

ALTER TABLE ponto_coleta ALTER COLUMN id RESTART WITH (
    SELECT COALESCE(MAX(id), 0) + 1 FROM ponto_coleta
);

ALTER TABLE rota ALTER COLUMN id RESTART WITH (
    SELECT COALESCE(MAX(id), 0) + 1 FROM rota
);

ALTER TABLE conexao_bairro ALTER COLUMN id RESTART WITH (
    SELECT COALESCE(MAX(id), 0) + 1 FROM conexao_bairro
);

ALTER TABLE itinerario ALTER COLUMN id RESTART WITH (
    SELECT COALESCE(MAX(id), 0) + 1 FROM itinerario
);