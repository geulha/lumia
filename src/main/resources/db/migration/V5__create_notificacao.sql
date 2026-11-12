-- Migração V5: Criação da tabela de Notificações do Sistema
CREATE TABLE notificacao (
    id SERIAL PRIMARY KEY,
    usuario_id INT NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
    titulo VARCHAR(150) NOT NULL,
    mensagem TEXT NOT NULL,
    link VARCHAR(255),
    tipo VARCHAR(50) NOT NULL,
    lida BOOLEAN NOT NULL DEFAULT FALSE,
    criado TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_notificacao_usuario_lida ON notificacao(usuario_id, lida);
CREATE INDEX idx_notificacao_criado ON notificacao(criado DESC);

-- Notificações iniciais de boas-vindas para os usuários semeados
INSERT INTO notificacao (usuario_id, titulo, mensagem, link, tipo, lida, criado)
SELECT id, 'Bem-vindo(a) ao Lumia!', 'Seu acesso ao Portal do Colaborador foi configurado com sucesso. Explore serviços, tire dúvidas na Base de Conhecimento e acompanhe solicitações.', '/colaborador', 'TICKET_ATUALIZADO', FALSE, CURRENT_TIMESTAMP
FROM usuario WHERE ativo = TRUE;
