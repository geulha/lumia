-- Migração V6: Notificação para aprovações pendentes existentes no seed
INSERT INTO notificacao (usuario_id, titulo, mensagem, link, tipo, lida, criado)
SELECT a.aprovador_id,
       'Nova aprovação pendente: REQ0083619',
       'Carol Sturka abriu o chamado ''Agendamento de Férias'' (REQ0083619) que aguarda sua avaliação e aprovação.',
       '/aprovacoes/' || a.id,
       'APROVACAO_PENDENTE',
       FALSE,
       a.criado
FROM aprovacao a
WHERE a.ticket_id = 83619 AND a.decisao = 'PENDENTE';
