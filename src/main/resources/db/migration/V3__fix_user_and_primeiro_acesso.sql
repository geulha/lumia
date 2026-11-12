-- V3__fix_user_and_primeiro_acesso.sql
-- Adiciona coluna de controle de primeiro acesso / troca obrigatória de senha
ALTER TABLE usuario ADD COLUMN IF NOT EXISTS primeiro_acesso BOOLEAN NOT NULL DEFAULT FALSE;

-- Restaura o nome e perfil de RH de Zosia Hivemind caso tenham sido sobrescritos por binding acidental
UPDATE usuario
SET nome = 'Zosia Hivemind',
    perfil = 'RH',
    posto = 'Especialista em Recursos Humanos'
WHERE id = 1 OR email = 'zosia.hivemind@lumia.com.br';
