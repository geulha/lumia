# LUMIA – Registro de Decisões de Arquitetura e Escopo (docs/DECISOES.md)

Este documento registra todas as decisões técnicas e de escopo tomadas durante o desenvolvimento do LUMIA – Portal do Colaborador, em conformidade com as regras de conduta do projeto (Seção 0 da especificação).

---

## 1. Atributo `categoria` na tabela `tipo_solicitacao`
- **Contexto**: A tela do colaborador (protótipo da Seção 7) apresenta 4 categorias de acesso rápido: *Benefícios*, *Folha de Pagamento*, *Documentos* e *Férias*. O fluxo permite clicar na categoria e visualizar apenas os formulários pertinentes a ela.
- **Decisão**: Foi adicionada a coluna `categoria VARCHAR(100)` na tabela `tipo_solicitacao`, mantendo exata simetria com a coluna `categoria VARCHAR(100)` já existente na tabela `artigo`. Dessa forma, atende-se ao protótipo e ao requisito de categorização sem criar tabelas adicionais e com integridade total.

## 2. Armazenamento Seguro de Anexos
- **Contexto**: A Seção 8 (RNF02) exige que arquivos enviados nos formulários dinâmicos sejam restritos por tipo/tamanho e salvos fora do diretório público web.
- **Decisão**: Foi implementado o serviço `FileStorageService` que salva arquivos em diretório configurado via propriedade `lumia.upload.dir=./uploads`. Os arquivos recebem nomes únicos gerados com UUID para evitar sobrescrita e injeção de caminho (`path traversal`). Tipos permitidos: PDF, PNG, JPG, JPEG, DOC, DOCX. Limite de tamanho: 10MB por arquivo. O download é feito através de endpoint autenticado (`/tickets/{ticketId}/anexos/{respostaId}`), garantindo validação de permissão.

## 3. Fluxo de Solicitação com Aprovação sem Gestor Imediato
- **Contexto**: O formulário pode exigir aprovação (`requer_aprovacao = true`), porém o colaborador solicitante pode não ter um gestor imediato vinculado (`usuario.gestor_id IS NULL`).
- **Decisão**: Caso o colaborador não possua gestor imediato cadastrado, o chamado é criado diretamente com status `ABERTO` e encaminhado à fila do RH, sendo gerada uma interação de sistema no histórico informando a dispensa de aprovação hierárquica por ausência de gestor imediato.

## 4. Identificadores Visuais Padronizados
- **Contexto**: Os protótipos de tela e a Seção 4 estipulam que o ticket possui identificador no formato `REQ` + 7 dígitos (ex.: `REQ0031637`), e os artigos da base de conhecimento exibem códigos como `ART0183`.
- **Decisão**: 
  - Tickets: gerado visualmente pelo método `ticket.getCodigoFormatado()` com máscara `String.format("REQ%07d", id)`.
  - Artigos: gerado visualmente por `artigo.getCodigoFormatado()` com máscara `String.format("ART%04d", id)`.

## 5. Banco de Dados e Compatibilidade de Execução
- **Contexto**: A especificação exige PostgreSQL com `docker-compose.yml`, Flyway/scripts SQL e testes automatizados.
- **Decisão**: O perfil padrão (`application.properties`) conecta-se ao PostgreSQL na porta 5432 (disponibilizado via `docker-compose.yml`). Para testes unitários e de integração (`mvn test`) sem exigir que o daemon Docker esteja rodando na máquina durante builds locais ou CI, configurou-se perfil de teste com banco H2 em modo de compatibilidade PostgreSQL, garantindo que `mvn test` passe com 100% de confiabilidade.

## 6. Geração de Senha Temporária e Credenciais
- **Contexto**: O RF01 define que o RH cria usuários e o sistema gera uma senha aleatória temporária.
- **Decisão**: Ao cadastrar um novo usuário, o sistema gera uma senha aleatória forte de 10 caracteres (letras maiúsculas, minúsculas, dígitos e caracteres especiais), calcula o hash BCrypt para persistência e exibe a senha gerada em alerta de sucesso na interface do RH com botão para copiar.
