-- Migração V4: Adiciona visibilidade por perfil/posto e ícone nos artigos da Base de Conhecimento
ALTER TABLE artigo ADD COLUMN visivel_para_perfil VARCHAR(20);
ALTER TABLE artigo ADD COLUMN visivel_para_posto VARCHAR(100);
ALTER TABLE artigo ADD COLUMN icone VARCHAR(50) DEFAULT 'bi-file-earmark-text';

-- Configuração inicial dos ícones para os artigos semeados
UPDATE artigo SET icone = 'bi-calendar-check' WHERE categoria = 'Férias';
UPDATE artigo SET icone = 'bi-cash-coin' WHERE categoria = 'Folha de Pagamento';
UPDATE artigo SET icone = 'bi-heart-pulse' WHERE categoria = 'Benefícios';
UPDATE artigo SET icone = 'bi-file-earmark-text' WHERE icone IS NULL;
