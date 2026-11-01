-- LUMIA: Dados Iniciais de Seed (V2__seed_data.sql)
-- Senha padrão para todos os usuários iniciais: senha123
-- Hash BCrypt gerado: $2a$10$HVcWD46.P4/jig8eQKHbouSVIaDQPAk6/9WTbSW1BcCRhxWeVDoS.

-- 1. USUÁRIOS
-- RH
INSERT INTO usuario (id, nome, email, senha_hash, cpf, perfil, posto, gestor_id, ativo, bloqueado, criado)
VALUES (1, 'Zosia Hivemind', 'zosia.hivemind@lumia.com.br', '$2a$10$HVcWD46.P4/jig8eQKHbouSVIaDQPAk6/9WTbSW1BcCRhxWeVDoS.', '11122233344', 'RH', 'Especialista em Recursos Humanos', NULL, TRUE, FALSE, CURRENT_TIMESTAMP - INTERVAL '60' DAY);

-- Gestor (Colaboradora com liderança)
INSERT INTO usuario (id, nome, email, senha_hash, cpf, perfil, posto, gestor_id, ativo, bloqueado, criado)
VALUES (2, 'Helen Sturka', 'helen.sturka@lumia.com.br', '$2a$10$HVcWD46.P4/jig8eQKHbouSVIaDQPAk6/9WTbSW1BcCRhxWeVDoS.', '22233344455', 'COLABORADOR', 'Gerente de Operações', NULL, TRUE, FALSE, CURRENT_TIMESTAMP - INTERVAL '50' DAY);

-- Colaboradores (liderados por Helen Sturka)
INSERT INTO usuario (id, nome, email, senha_hash, cpf, perfil, posto, gestor_id, ativo, bloqueado, criado)
VALUES (3, 'Carol Sturka', 'carol.sturka@lumia.com.br', '$2a$10$HVcWD46.P4/jig8eQKHbouSVIaDQPAk6/9WTbSW1BcCRhxWeVDoS.', '33344455566', 'COLABORADOR', 'Analista Financeiro Pleno', 2, TRUE, FALSE, CURRENT_TIMESTAMP - INTERVAL '40' DAY);

INSERT INTO usuario (id, nome, email, senha_hash, cpf, perfil, posto, gestor_id, ativo, bloqueado, criado)
VALUES (4, 'Giulia Lain', 'giulia.lain@lumia.com.br', '$2a$10$HVcWD46.P4/jig8eQKHbouSVIaDQPAk6/9WTbSW1BcCRhxWeVDoS.', '44455566677', 'COLABORADOR', 'Engenheira de Software', 2, TRUE, FALSE, CURRENT_TIMESTAMP - INTERVAL '35' DAY);

INSERT INTO usuario (id, nome, email, senha_hash, cpf, perfil, posto, gestor_id, ativo, bloqueado, criado)
VALUES (5, 'Lauren Amici', 'lauren.amici@lumia.com.br', '$2a$10$HVcWD46.P4/jig8eQKHbouSVIaDQPAk6/9WTbSW1BcCRhxWeVDoS.', '55566677788', 'COLABORADOR', 'Designer de Produto', 2, TRUE, FALSE, CURRENT_TIMESTAMP - INTERVAL '30' DAY);

INSERT INTO usuario (id, nome, email, senha_hash, cpf, perfil, posto, gestor_id, ativo, bloqueado, criado)
VALUES (6, 'Lorena Barcellos', 'lorena.barcellos@lumia.com.br', '$2a$10$HVcWD46.P4/jig8eQKHbouSVIaDQPAk6/9WTbSW1BcCRhxWeVDoS.', '66677788899', 'COLABORADOR', 'Analista de QA', 2, TRUE, FALSE, CURRENT_TIMESTAMP - INTERVAL '25' DAY);


-- 2. TIPOS DE SOLICITAÇÃO (FORMULÁRIOS)
INSERT INTO tipo_solicitacao (id, nome, descricao, requer_aprovacao, visivel_para_perfil, visivel_para_posto, categoria, ativo, criado_por_id, criado)
VALUES (1, 'Agendamento de Férias', 'Solicitação de período de descanso anual remunerado. Requer aprovação do gestor imediato.', TRUE, 'COLABORADOR', NULL, 'Férias', TRUE, 1, CURRENT_TIMESTAMP - INTERVAL '40' DAY);

INSERT INTO tipo_solicitacao (id, nome, descricao, requer_aprovacao, visivel_para_perfil, visivel_para_posto, categoria, ativo, criado_por_id, criado)
VALUES (2, 'Correção de Folha de Pagamento', 'Ajuste ou revisão de lançamentos em folha, horas extras, descontos ou adicionais.', FALSE, 'COLABORADOR', NULL, 'Folha de Pagamento', TRUE, 1, CURRENT_TIMESTAMP - INTERVAL '38' DAY);

INSERT INTO tipo_solicitacao (id, nome, descricao, requer_aprovacao, visivel_para_perfil, visivel_para_posto, categoria, ativo, criado_por_id, criado)
VALUES (3, 'Adesão ao Plano de Saúde', 'Inclusão ou alteração de plano de saúde médico/odontológico corporativo.', FALSE, 'COLABORADOR', NULL, 'Benefícios', TRUE, 1, CURRENT_TIMESTAMP - INTERVAL '35' DAY);

INSERT INTO tipo_solicitacao (id, nome, descricao, requer_aprovacao, visivel_para_perfil, visivel_para_posto, categoria, ativo, criado_por_id, criado)
VALUES (4, 'Desligamento Voluntário', 'Pedido de demissão e agendamento de exame demissional e homologação.', TRUE, 'COLABORADOR', NULL, 'Documentos', TRUE, 1, CURRENT_TIMESTAMP - INTERVAL '30' DAY);

INSERT INTO tipo_solicitacao (id, nome, descricao, requer_aprovacao, visivel_para_perfil, visivel_para_posto, categoria, ativo, criado_por_id, criado)
VALUES (5, 'Solicitação de Vale Refeição/Alimentação', 'Alteração de divisão de saldo ou emissão de segunda via do cartão benefício.', FALSE, 'COLABORADOR', NULL, 'Benefícios', TRUE, 1, CURRENT_TIMESTAMP - INTERVAL '28' DAY);

INSERT INTO tipo_solicitacao (id, nome, descricao, requer_aprovacao, visivel_para_perfil, visivel_para_posto, categoria, ativo, criado_por_id, criado)
VALUES (6, 'Declaração de Trabalho e Rendimentos', 'Emissão de comprovante de vínculo e declaração de rendimentos para fins bancários/escolares.', FALSE, 'COLABORADOR', NULL, 'Documentos', TRUE, 1, CURRENT_TIMESTAMP - INTERVAL '25' DAY);


-- 3. CAMPOS DE FORMULÁRIO DINÂMICOS
-- Formulário 1: Agendamento de Férias
INSERT INTO campo_formulario (id, tipo_solicitacao_id, rotulo, tipo_campo, obrigatorio, ordem, opcoes)
VALUES (1, 1, 'Data de Início das Férias', 'DATA', TRUE, 1, NULL);

INSERT INTO campo_formulario (id, tipo_solicitacao_id, rotulo, tipo_campo, obrigatorio, ordem, opcoes)
VALUES (2, 1, 'Quantidade de Dias', 'NUMERO', TRUE, 2, NULL);

INSERT INTO campo_formulario (id, tipo_solicitacao_id, rotulo, tipo_campo, obrigatorio, ordem, opcoes)
VALUES (3, 1, 'Deseja Adiantamento de 13º Salário?', 'SELECAO', TRUE, 3, '["Sim", "Não"]');

INSERT INTO campo_formulario (id, tipo_solicitacao_id, rotulo, tipo_campo, obrigatorio, ordem, opcoes)
VALUES (4, 1, 'Observações para o Gestor', 'TEXTO_LONGO', FALSE, 4, NULL);

-- Formulário 2: Correção de Folha de Pagamento
INSERT INTO campo_formulario (id, tipo_solicitacao_id, rotulo, tipo_campo, obrigatorio, ordem, opcoes)
VALUES (5, 2, 'Mês de Referência', 'SELECAO', TRUE, 1, '["Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"]');

INSERT INTO campo_formulario (id, tipo_solicitacao_id, rotulo, tipo_campo, obrigatorio, ordem, opcoes)
VALUES (6, 2, 'Descrição da Divergência Encontrada', 'TEXTO_LONGO', TRUE, 2, NULL);

INSERT INTO campo_formulario (id, tipo_solicitacao_id, rotulo, tipo_campo, obrigatorio, ordem, opcoes)
VALUES (7, 2, 'Comprovante ou Espelho de Ponto', 'ANEXO', FALSE, 3, NULL);

-- Formulário 3: Adesão ao Plano de Saúde
INSERT INTO campo_formulario (id, tipo_solicitacao_id, rotulo, tipo_campo, obrigatorio, ordem, opcoes)
VALUES (8, 3, 'Plano Escolhido', 'SELECAO', TRUE, 1, '["Básico Nacional", "Especial Apartamento", "Master Executivo"]');

INSERT INTO campo_formulario (id, tipo_solicitacao_id, rotulo, tipo_campo, obrigatorio, ordem, opcoes)
VALUES (9, 3, 'Possui Dependentes?', 'SELECAO', TRUE, 2, '["Sim", "Não"]');

INSERT INTO campo_formulario (id, tipo_solicitacao_id, rotulo, tipo_campo, obrigatorio, ordem, opcoes)
VALUES (10, 3, 'Documento de Identificação (RG/CNH)', 'ANEXO', TRUE, 3, NULL);

-- Formulário 4: Desligamento Voluntário
INSERT INTO campo_formulario (id, tipo_solicitacao_id, rotulo, tipo_campo, obrigatorio, ordem, opcoes)
VALUES (11, 4, 'Último Dia Trabalhado Pretendido', 'DATA', TRUE, 1, NULL);

INSERT INTO campo_formulario (id, tipo_solicitacao_id, rotulo, tipo_campo, obrigatorio, ordem, opcoes)
VALUES (12, 4, 'Cumprirá Aviso Prévio?', 'SELECAO', TRUE, 2, '["Sim, trabalhado", "Não, indenizado"]');

INSERT INTO campo_formulario (id, tipo_solicitacao_id, rotulo, tipo_campo, obrigatorio, ordem, opcoes)
VALUES (13, 4, 'Carta de Demissão Assinada', 'ANEXO', TRUE, 3, NULL);


-- 4. ARTIGOS DA BASE DE CONHECIMENTO (conforme protótipo)
INSERT INTO artigo (id, titulo, conteudo, categoria, publicado, autor_id, criado, atualizado)
VALUES (183, 'Orientações sobre Agendamento de Férias', 'Confira as regras e prazos para agendar suas férias anuais. O pedido deve ser realizado com pelo menos 30 dias de antecedência da data desejada e requer a anuência da gestão imediata.', 'Férias', TRUE, 1, CURRENT_TIMESTAMP - INTERVAL '3' HOUR, CURRENT_TIMESTAMP - INTERVAL '3' HOUR);

INSERT INTO artigo (id, titulo, conteudo, categoria, publicado, autor_id, criado, atualizado)
VALUES (182, 'Atente-se ao Bater o Ponto', 'Informações sobre marcação de ponto eletrônico, tolerância de minutos, justificativa de atrasos e atestados médicos na plataforma.', 'Folha de Pagamento', TRUE, 1, CURRENT_TIMESTAMP - INTERVAL '5' DAY, CURRENT_TIMESTAMP - INTERVAL '5' DAY);

INSERT INTO artigo (id, titulo, conteudo, categoria, publicado, autor_id, criado, atualizado)
VALUES (181, 'Orientações sobre Horas Extras', 'Procedimentos para realização de banco de horas ou pagamento de horas extras com aprovação prévia da liderança.', 'Folha de Pagamento', TRUE, 1, CURRENT_TIMESTAMP - INTERVAL '15' DAY, CURRENT_TIMESTAMP - INTERVAL '15' DAY);

INSERT INTO artigo (id, titulo, conteudo, categoria, publicado, autor_id, criado, atualizado)
VALUES (180, 'Calendário de Feriados Nacionais', 'Confira a grade completa de feriados nacionais e pontos facultativos para o ano corrente da nossa organização.', 'Benefícios', TRUE, 1, CURRENT_TIMESTAMP - INTERVAL '20' DAY, CURRENT_TIMESTAMP - INTERVAL '20' DAY);


-- 5. TICKETS (Exemplos que cobrem todos os status e reproduzem com precisão os protótipos de tela)
-- REQ0083619 (id: 83619) - Carol Sturka - PENDENTE (há 56 minutos)
INSERT INTO ticket (id, colaborador_id, tipo_solicitacao_id, status, analista_id, criado, atualizado)
VALUES (83619, 3, 1, 'PENDENTE', NULL, CURRENT_TIMESTAMP - INTERVAL '56' MINUTE, CURRENT_TIMESTAMP - INTERVAL '56' MINUTE);

INSERT INTO resposta_formulario (ticket_id, campo_id, valor_texto, caminho_arquivo)
VALUES (83619, 1, '2026-12-15', NULL),
       (83619, 2, '15', NULL),
       (83619, 3, 'Não', NULL),
       (83619, 4, 'Planejamento alinhado para a segunda quinzena de dezembro.', NULL);

INSERT INTO aprovacao (ticket_id, aprovador_id, decisao, comentario, decidido_em, criado)
VALUES (83619, 2, 'PENDENTE', NULL, NULL, CURRENT_TIMESTAMP - INTERVAL '56' MINUTE);

INSERT INTO interacao_ticket (ticket_id, autor_id, tipo, conteudo, status_para, criado)
VALUES (83619, 3, 'COMENTARIO', 'Ticket aberto e enviado para aprovação da gestora Helen Sturka.', 'PENDENTE', CURRENT_TIMESTAMP - INTERVAL '56' MINUTE);


-- REQ0081637 (id: 81637) - Carol Sturka - ABERTO (um dia atrás)
INSERT INTO ticket (id, colaborador_id, tipo_solicitacao_id, status, analista_id, criado, atualizado)
VALUES (81637, 3, 2, 'ABERTO', 1, CURRENT_TIMESTAMP - INTERVAL '1' DAY, CURRENT_TIMESTAMP - INTERVAL '1' DAY);

INSERT INTO resposta_formulario (ticket_id, campo_id, valor_texto, caminho_arquivo)
VALUES (81637, 5, 'Maio', NULL),
       (81637, 6, 'Não foi creditado o adicional noturno correspondente ao plantão do dia 12.', NULL);

INSERT INTO interacao_ticket (ticket_id, autor_id, tipo, conteudo, status_para, criado)
VALUES (81637, 3, 'COMENTARIO', 'Solicito verificação na folha referente ao adicional noturno.', 'ABERTO', CURRENT_TIMESTAMP - INTERVAL '1' DAY);


-- REQ0036284 (id: 36284) - Carol Sturka - REPROVADO (há uma semana)
INSERT INTO ticket (id, colaborador_id, tipo_solicitacao_id, status, analista_id, criado, atualizado)
VALUES (36284, 3, 1, 'REPROVADO', NULL, CURRENT_TIMESTAMP - INTERVAL '7' DAY, CURRENT_TIMESTAMP - INTERVAL '6' DAY);

INSERT INTO resposta_formulario (ticket_id, campo_id, valor_texto, caminho_arquivo)
VALUES (36284, 1, '2026-07-01', NULL),
       (36284, 2, '30', NULL),
       (36284, 3, 'Sim', NULL);

INSERT INTO aprovacao (ticket_id, aprovador_id, decisao, comentario, decidido_em, criado)
VALUES (36284, 2, 'REJEITADO', 'Período coincidente com o fechamento semestral contábil da equipe. Favor reprogramar para o mês seguinte.', CURRENT_TIMESTAMP - INTERVAL '6' DAY, CURRENT_TIMESTAMP - INTERVAL '7' DAY);

INSERT INTO interacao_ticket (ticket_id, autor_id, tipo, conteudo, status_para, criado)
VALUES (36284, 2, 'MUDANCA_STATUS', 'Solicitação reprovada pelo gestor imediato: Período coincidente com o fechamento semestral contábil.', 'REPROVADO', CURRENT_TIMESTAMP - INTERVAL '6' DAY);


-- REQ0007762 (id: 7762) - Carol Sturka - CONCLUIDO (09/11/2025)
INSERT INTO ticket (id, colaborador_id, tipo_solicitacao_id, status, analista_id, criado, atualizado)
VALUES (7762, 3, 3, 'CONCLUIDO', 1, CURRENT_TIMESTAMP - INTERVAL '35' DAY, CURRENT_TIMESTAMP - INTERVAL '30' DAY);

INSERT INTO resposta_formulario (ticket_id, campo_id, valor_texto, caminho_arquivo)
VALUES (7762, 8, 'Especial Apartamento', NULL),
       (7762, 9, 'Não', NULL);

INSERT INTO interacao_ticket (ticket_id, autor_id, tipo, conteudo, status_para, criado)
VALUES (7762, 1, 'DEVOLUTIVA', 'Adesão efetuada com sucesso junto à operadora. A carteirinha virtual foi enviada ao seu e-mail corporativo.', 'CONCLUIDO', CURRENT_TIMESTAMP - INTERVAL '30' DAY);


-- REQ0007362 (id: 7362) - Giulia Lain - EM_ANDAMENTO (analista Zosia)
INSERT INTO ticket (id, colaborador_id, tipo_solicitacao_id, status, analista_id, criado, atualizado)
VALUES (7362, 4, 2, 'EM_ANDAMENTO', 1, CURRENT_TIMESTAMP - INTERVAL '2' DAY, CURRENT_TIMESTAMP - INTERVAL '1' DAY);

INSERT INTO resposta_formulario (ticket_id, campo_id, valor_texto, caminho_arquivo)
VALUES (7362, 5, 'Junho', NULL),
       (7362, 6, 'Ajuste de cálculo de DSR sobre horas extras computadas.', NULL);

INSERT INTO interacao_ticket (ticket_id, autor_id, tipo, conteudo, status_para, criado)
VALUES (7362, 1, 'COMENTARIO', 'Ticket atribuído a Zosia Hivemind. Estamos calculando o espelho retificador.', 'EM_ANDAMENTO', CURRENT_TIMESTAMP - INTERVAL '1' DAY);


-- REQ0007154 (id: 7154) - Lauren Amici - PENDENTE
INSERT INTO ticket (id, colaborador_id, tipo_solicitacao_id, status, analista_id, criado, atualizado)
VALUES (7154, 5, 1, 'PENDENTE', NULL, CURRENT_TIMESTAMP - INTERVAL '3' DAY, CURRENT_TIMESTAMP - INTERVAL '3' DAY);

INSERT INTO resposta_formulario (ticket_id, campo_id, valor_texto, caminho_arquivo)
VALUES (7154, 1, '2026-08-10', NULL),
       (7154, 2, '20', NULL),
       (7154, 3, 'Não', NULL);

INSERT INTO aprovacao (ticket_id, aprovador_id, decisao, comentario, decidido_em, criado)
VALUES (7154, 2, 'PENDENTE', NULL, NULL, CURRENT_TIMESTAMP - INTERVAL '3' DAY);


-- REQ0006789 (id: 6789) - Helen Sturka - CONCLUIDO
INSERT INTO ticket (id, colaborador_id, tipo_solicitacao_id, status, analista_id, criado, atualizado)
VALUES (6789, 2, 4, 'CONCLUIDO', 1, CURRENT_TIMESTAMP - INTERVAL '10' DAY, CURRENT_TIMESTAMP - INTERVAL '5' DAY);

INSERT INTO resposta_formulario (ticket_id, campo_id, valor_texto, caminho_arquivo)
VALUES (6789, 11, '2026-06-30', NULL),
       (6789, 12, 'Sim, trabalhado', NULL);

INSERT INTO interacao_ticket (ticket_id, autor_id, tipo, conteudo, status_para, criado)
VALUES (6789, 1, 'DEVOLUTIVA', 'Processamento concluído. O kit rescisório e agendamento médico foram formalizados.', 'CONCLUIDO', CURRENT_TIMESTAMP - INTERVAL '5' DAY);
