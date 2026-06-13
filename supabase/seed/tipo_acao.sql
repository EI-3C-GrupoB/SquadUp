-- Seed da tabela de referência `tipo_acao`.
-- Estes tipos de ação são necessários para o LiveMatch: sem eles,
-- `findActionTypeId()` devolve null e os eventos perdem o tipo ao recarregar.
-- Os nomes são reconhecidos por LiveMatchRepository.findActionTypeId / toMatchEventType.
INSERT INTO tipo_acao (nome) VALUES
    ('Golo'),
    ('Cartão Amarelo'),
    ('Cartão Vermelho'),
    ('Falta'),
    ('Substituição'),
    ('Timeout'),
    ('Canto'),
    ('Fora de Jogo'),
    ('Defesa')
ON CONFLICT DO NOTHING;
