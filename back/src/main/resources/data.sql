MERGE INTO usuario (id, nome, email, senha, perfil, ativo) KEY(id) VALUES
    (1, 'Administrador do Sistema', 'admin@greenlog.com', 'admin123', 'ADMIN', true),
    (2, 'Usuário Padrão', 'user@greenlog.com', 'user123', 'USER', true);

MERGE INTO tipo_residuo (id, nome, ativo) KEY(id) VALUES
    (1, 'Orgânico', true),
    (2, 'Plástico', true),
    (3, 'Metal', true),
    (4, 'Vidro', true),
    (5, 'Papel', true),
    (6, 'Eletrônico', true);

MERGE INTO caminhao (id, placa, motorista, capacidade_kg, ativo) KEY(id) VALUES
    (1, 'ABC1D23', 'João Silva', 5000, true),
    (2, 'XYZ9A88', 'Carlos Moura', 3500, true),
    (3, 'QWE4F55', 'Ana Pereira', 4500, true);

MERGE INTO caminhao_residuo (caminhao_id, tipo_residuo_id) KEY(caminhao_id, tipo_residuo_id) VALUES
    (1, 1), (1, 2), (1, 3),
    (2, 2), (2, 5),
    (3, 1), (3, 4), (3, 6);

MERGE INTO bairro (id, nome, descricao, ativo) KEY(id) VALUES
    (1, 'Jardim América', 'Bairro importado', true),
    (2, 'Centro', 'Bairro importado', true),
    (3, 'Setor Leste', 'Bairro importado', true),
    (4, 'Vila Nova', 'Bairro importado', true),
    (5, 'Alto da Serra', 'Bairro importado', true),
    (6, 'Setor Oeste', 'Bairro importado', true),
    (7, 'Distrito Industrial', 'Bairro importado', true),
    (8, 'Residencial Esperança', 'Bairro importado', true),
    (9, 'Recanto Verde', 'Bairro importado', true),
    (10, 'Ecoparque Sul', 'Bairro importado', true),
    (11, 'Nova Alvorada', 'Bairro importado', true),
    (12, 'Setor das Palmeiras', 'Bairro importado', true),
    (13, 'Colina Azul', 'Bairro importado', true),
    (14, 'Bela Vista', 'Bairro importado', true),
    (15, 'Morada do Sol', 'Bairro importado', true),
    (16, 'Setor Central II', 'Bairro importado', true),
    (17, 'Lago Azul', 'Bairro importado', true),
    (18, 'Residencial Florença', 'Bairro importado', true),
    (19, 'Setor Industrial Norte', 'Bairro importado', true),
    (20, 'Vale do Cerrado', 'Bairro importado', true);

MERGE INTO ponto_coleta (id, nome_ponto, nome_responsavel, contato, email, endereco, bairro_id, ativo) KEY(id) VALUES 
    (1, 'Ponto Coleta 1', 'Marcos Teixeira', '(62) 99872-9285', 'joão.silva@ecoville.gov', 'Rua A, nº 291', 16, true),
    (2, 'Ponto Coleta 2', 'Paula Mendes', '(62) 96663-1427', 'joão.silva@ecoville.gov', 'Rua C, nº 109', 16, true),
    (3, 'Ponto Coleta 3', 'João Silva', '(62) 96663-1427', 'fernanda.rocha@ecoville.gov', 'Rua C, nº 942', 7, true),
    (4, 'Ponto Coleta 4', 'Ana Souza', '(62) 91541-3369', 'maria.oliveira@ecoville.gov', 'Rua B, nº 182', 12, true),
    (5, 'Ponto Coleta 5', 'Carlos Lima', '(62) 96663-1427', 'ricardo.alves@ecoville.gov', 'Rua C, nº 607', 5, true),
    (6, 'Ponto Coleta 6', 'Maria Oliveira', '(62) 99076-3032', 'ana.souza@ecoville.gov', 'Rua C, nº 494', 19, true),
    (7, 'Ponto Coleta 7', 'Fernanda Rocha', '(62) 99076-3032', 'cláudio.pinto@ecoville.gov', 'Rua C, nº 257', 19, true),
    (8, 'Ponto Coleta 8', 'Paula Mendes', '(62) 99872-9285', 'ricardo.alves@ecoville.gov', 'Rua C, nº 457', 8, true),
    (9, 'Ponto Coleta 9', 'Marcos Teixeira', '(62) 99872-9285', 'paula.mendes@ecoville.gov', 'Rua B, nº 859', 18, true),
    (10, 'Ponto Coleta 10', 'Marcos Teixeira', '(62) 97392-2067', 'cláudio.pinto@ecoville.gov', 'Rua B, nº 666', 1, true),
    (11, 'Ponto Coleta 11', 'Maria Oliveira', '(62) 95599-8027', 'marcos.teixeira@ecoville.gov', 'Rua B, nº 198', 1, true),
    (12, 'Ponto Coleta 12', 'João Silva', '(62) 94926-3267', 'cláudio.pinto@ecoville.gov', 'Rua D, nº 665', 10, true),
    (13, 'Ponto Coleta 13', 'Carlos Lima', '(62) 98630-6093', 'paula.mendes@ecoville.gov', 'Rua C, nº 933', 10, true),
    (14, 'Ponto Coleta 14', 'Marcos Teixeira', '(62) 98630-6093', 'ricardo.alves@ecoville.gov', 'Rua B, nº 938', 6, true),
    (15, 'Ponto Coleta 15', 'Fernanda Rocha', '(62) 91714-2688', 'juliana.reis@ecoville.gov', 'Rua A, nº 404', 3, true),
    (16, 'Ponto Coleta 16', 'Maria Oliveira', '(62) 97183-7080', 'juliana.reis@ecoville.gov', 'Rua A, nº 789', 4, true);

MERGE INTO ponto_residuo (ponto_coleta_id, tipo_residuo_id) KEY(ponto_coleta_id, tipo_residuo_id) VALUES
    (1, 3), -- Ponto 1: Metal
    (2, 3), (2, 5), (2, 2), -- Ponto 2: Metal, Papel, Plástico
    (3, 1), (3, 3), -- Ponto 3: Orgânico, Metal
    (4, 5), (4, 1), (4, 2), -- Ponto 4: Papel, Orgânico, Plástico
    (5, 5), (5, 3), (5, 1), -- Ponto 5: Papel, Metal, Orgânico
    (6, 3), (6, 5), -- Ponto 6: Metal, Papel
    (7, 5), (7, 3), (7, 2), -- Ponto 7: Papel, Metal, Plástico
    (8, 2), (8, 3), (8, 1), -- Ponto 8: Plástico, Metal, Orgânico
    (9, 2), (9, 5), -- Ponto 9: Plástico, Papel
    (10, 1), -- Ponto 10: Orgânico
    (11, 3), -- Ponto 11: Metal
    (12, 2), (12, 3), -- Ponto 12: Plástico, Metal
    (13, 3), -- Ponto 13: Metal
    (14, 2), -- Ponto 14: Plástico
    (15, 1), (15, 5), -- Ponto 15: Orgânico, Papel
    (16, 1), (16, 3); -- Ponto 16: Orgânico, Metal

MERGE INTO rota (id, nome) KEY(id) VALUES
    (1, 'Rota Norte'),
    (2, 'Rota Leste'),
    (3, 'Rota Sul');

MERGE INTO rota_bairro (rota_id, bairro_id) KEY(rota_id, bairro_id) VALUES
    (1, 1), (1, 2),
    (2, 3), (2, 5),
    (3, 4), (3, 1);

MERGE INTO conexao_bairro (id, bairro_origem_id, bairro_destino_id, distancia, ativo) KEY(id) VALUES
    (1, 9, 16, 6.4, true),
    (2, 15, 19, 8.3, true),
    (3, 17, 7, 1.2, true),
    (4, 3, 5, 12.2, true),
    (5, 12, 4, 14.0, true),
    (6, 13, 7, 9.2, true),
    (7, 13, 6, 19.2, true),
    (8, 5, 9, 13.2, true),
    (9, 16, 3, 3.4, true),
    (10, 8, 10, 12.8, true),
    (11, 20, 1, 14.4, true),
    (12, 14, 3, 18.1, true),
    (13, 2, 18, 1.9, true),
    (14, 6, 11, 15.7, true),
    (15, 1, 17, 14.5, true),
    (16, 3, 4, 19.2, true),
    (17, 14, 19, 18.9, true),
    (18, 15, 18, 18.5, true),
    (19, 20, 2, 14.7, true),
    (20, 15, 20, 12.7, true),
    (21, 17, 15, 7.9, true),
    (22, 4, 12, 6.4, true),
    (23, 5, 15, 8.6, true),
    (24, 6, 2, 13.4, true),
    (25, 14, 15, 9.4, true),
    (26, 9, 3, 18.7, true),
    (27, 18, 7, 1.7, true),
    (28, 13, 7, 17.5, true),
    (29, 18, 9, 9.0, true),
    (30, 15, 11, 18.3, true),
    (31, 3, 4, 3.0, true),
    (32, 7, 2, 13.9, true),
    (33, 20, 4, 7.7, true),
    (34, 5, 16, 14.3, true),
    (35, 13, 4, 12.8, true),
    (36, 1, 16, 13.4, true),
    (37, 14, 3, 14.3, true),
    (38, 2, 6, 16.7, true),
    (39, 11, 8, 16.6, true),
    (40, 11, 10, 4.6, true),
    (41, 4, 1, 7.0, true),
    (42, 11, 7, 14.4, true),
    (43, 13, 5, 6.2, true),
    (44, 9, 20, 2.7, true),
    (45, 13, 15, 8.3, true),
    (46, 17, 13, 16.3, true),
    (47, 10, 14, 7.9, true),
    (48, 8, 1, 17.9, true),
    (49, 9, 2, 19.3, true),
    (50, 16, 17, 18.4, true),
    (51, 6, 14, 9.0, true),
    (52, 2, 19, 5.1, true),
    (53, 6, 5, 1.3, true),
    (54, 2, 1, 1.4, true),
    (55, 20, 19, 3.7, true),
    (56, 20, 2, 6.5, true),
    (57, 4, 8, 13.1, true),
    (58, 4, 19, 3.8, true),
    (59, 16, 11, 2.8, true),
    (60, 13, 16, 7.8, true);

MERGE INTO itinerario (id, data, caminhao_id, rota_id) KEY(id) VALUES
    (1, DATE '2025-03-01', 1, 1),
    (2, DATE '2025-03-01', 2, 2),
    (3, DATE '2025-03-02', 3, 3),
    (4, DATE '2025-03-03', 1, 3);

ALTER TABLE USUARIO ALTER COLUMN id RESTART WITH (SELECT COALESCE(MAX(id), 0) + 1 FROM USUARIO);
ALTER TABLE bairro ALTER COLUMN id RESTART WITH (SELECT COALESCE(MAX(id), 0) + 1 FROM bairro);
ALTER TABLE tipo_residuo ALTER COLUMN id RESTART WITH (SELECT COALESCE(MAX(id), 0) + 1 FROM tipo_residuo);
ALTER TABLE caminhao ALTER COLUMN id RESTART WITH (SELECT COALESCE(MAX(id), 0) + 1 FROM caminhao);
ALTER TABLE ponto_coleta ALTER COLUMN id RESTART WITH (SELECT COALESCE(MAX(id), 0) + 1 FROM ponto_coleta);
ALTER TABLE rota ALTER COLUMN id RESTART WITH (SELECT COALESCE(MAX(id), 0) + 1 FROM rota);
ALTER TABLE conexao_bairro ALTER COLUMN id RESTART WITH (SELECT COALESCE(MAX(id), 0) + 1 FROM conexao_bairro);
ALTER TABLE itinerario ALTER COLUMN id RESTART WITH (SELECT COALESCE(MAX(id), 0) + 1 FROM itinerario);