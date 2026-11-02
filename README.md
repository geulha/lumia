# LUMIA: Portal do Colaborador & Atendimento de RH

**Projeto de Conclusão de Curso (TCC) – Curso Superior de Tecnologia em Análise e Desenvolvimento de Sistemas**  
**Faculdade de Tecnologia Baixada Santista – Rubens Lara (FATEC Santos)**  

**Integrantes:**
- Giulia Lain Sansanoski Rodrigues
- Lauren Amici Garcia
- Lorena Barcellos de Souza

**Orientador:** Prof. Davi Silvestre Moreira dos Reis  
**Ano:** 2026  

---

## 1. Visão Geral do Sistema

Em empresas de pequeno e médio porte, a comunicação entre colaboradores e o setor de Recursos Humanos frequentemente ocorre de forma descentralizada e informal (e-mails, conversas de corredor e aplicativos de mensagem instantânea), acarretando perda de prazos, ausência de histórico e sobrecarga operacional. 

O **LUMIA** soluciona essa problemática por meio de uma plataforma web unificada e independente, estruturada em duas frentes complementares:
1. **Portal do Colaborador:** Autoatendimento estruturado para abertura de solicitações ao RH através de formulários dinâmicos com upload de documentos, consulta a políticas internas via Base de Conhecimento (FAQ) e acompanhamento em tempo real do status e histórico de cada chamado.
2. **Painel do RH:** Workspace centralizado com indicadores quantitativos de chamados, fila de atendimento, atribuição de tickets a analistas, registro de devolutivas formais, gestão hierárquica de usuários e construtor de formulários personalizados.
3. **Fluxo de Aprovação Gestor ("Minhas Aprovações"):** Para solicitações que exigem anuência prévia da liderança (ex.: Agendamento de Férias), o chamado transita pela aba de aprovações do gestor imediato antes de prosseguir para o atendimento do RH.

---

## 2. Arquitetura e Stack Tecnológica

O sistema foi desenvolvido estritamente de acordo com a stack monolítica especificada no projeto:
- **Linguagem:** Java 21 (LTS)
- **Framework Principal:** Spring Boot 3.3.5
- **Renderização Server-Side:** Thymeleaf com Spring Security 6 Dialect (arquitetura MVC monolítica, sem API REST desacoplada)
- **Persistência de Dados:** Spring Data JPA + Hibernate ORM
- **Banco de Dados Principal:** PostgreSQL 16
- **Controle de Versão de Schema & Migrations:** Flyway Migrations (`db/migration/V1__init_schema.sql` e `V2__seed_data.sql`)
- **Segurança & Autenticação:** Spring Security com criptografia de senhas via **BCrypt**, proteção contra CSRF, proteção contra *Path Traversal* e controle de acesso baseado em perfis (**RBAC**: `COLABORADOR` e `RH`)
- **Build & Dependências:** Apache Maven
- **Conteinerização:** Docker Compose para banco de dados PostgreSQL

---

## 3. Modelo de Dados

O banco de dados relacional foi implementado com fidelidade absoluta ao diagrama de entidades da especificação:
- **`usuario`**: Cadastro central de colaboradores e profissionais de RH, com hierarquia de liderança via auto-relacionamento (`gestor_id`).
- **`tipo_solicitacao`**: Definição dinâmica dos formulários de chamados, com suporte a categorias de acesso rápido (*Benefícios*, *Folha de Pagamento*, *Documentos*, *Férias*), obrigatoriedade de aprovação e filtros por perfil e posto.
- **`campo_formulario`**: Construtor dinâmico de campos pertencentes a cada formulário (tipos suportados: `TEXTO`, `NUMERO`, `DATA`, `ANEXO`, `SELECAO`, `TEXTO_LONGO`).
- **`ticket`**: Chamados abertos, identificados com código formatado `REQ` + 7 dígitos (ex.: `REQ0083619`), status (`ABERTO`, `EM_ANDAMENTO`, `PENDENTE`, `CONCLUIDO`, `REPROVADO`) e analista de RH responsável.
- **`resposta_formulario`**: Valores informados pelos colaboradores e caminhos de arquivos anexados com segurança fora da raiz pública.
- **`interacao_ticket`**: Linha do tempo e histórico imutável de mensagens, devolutivas do RH e transições de status.
- **`aprovacao`**: Registro de parecer do gestor imediato (`PENDENTE`, `APROVADO`, `REJEITADO`).
- **`artigo`**: Base de conhecimento categorizada, com código formatado `ART` + 4 dígitos (ex.: `ART0183`).

*Para mais detalhes sobre decisões arquiteturais pontuais, consulte `docs/DECISOES.md`.*

---

## 4. Credenciais de Acesso de Demonstração (Seed)

O sistema já é inicializado com massa de dados de teste contemplando chamados em todos os status, artigos e contas com hierarquia completa:

| Perfil | Nome do Usuário | Cargo / Posto | E-mail de Login | Senha Padrão |
|---|---|---|---|---|
| **RH** | Zosia Hivemind | Especialista em RH | `zosia.hivemind@lumia.com.br` | `senha123` |
| **Colaboradora (Gestora)** | Helen Sturka | Gerente de Operações | `helen.sturka@lumia.com.br` | `senha123` |
| **Colaboradora** | Carol Sturka | Analista Financeiro | `carol.sturka@lumia.com.br` | `senha123` |
| **Colaboradora** | Giulia Lain | Engenheira de Software | `giulia.lain@lumia.com.br` | `senha123` |
| **Colaboradora** | Lauren Amici | Designer de Produto | `lauren.amici@lumia.com.br` | `senha123` |
| **Colaboradora** | Lorena Barcellos | Analista de QA | `lorena.barcellos@lumia.com.br` | `senha123` |

---

## 5. Instruções de Execução

### Opção A: Execução com Docker Compose (PostgreSQL)

1. **Subir o banco PostgreSQL:**
   ```bash
   docker compose up -d
   ```
2. **Executar a aplicação Spring Boot:**
   ```bash
   mvn spring-boot:run
   ```
3. Acessar no navegador: **`http://localhost:8080`**

---

### Opção B: Execução Rápida sem Docker (Banco H2 em modo PostgreSQL)

Caso não possua o Docker instalado ou deseje executar rapidamente em modo de avaliação autônoma:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=h2
```
A aplicação iniciará na porta **8080** utilizando banco de dados local com o mesmo schema e migrations Flyway.

---

### Executando os Testes Automatizados

Para executar a suíte completa de testes de integração e segurança:
```bash
mvn test
```

---

## 6. Guia de Teste dos Casos de Uso (UC01 a UC10)

1. **UC01 – Realizar Login:**
   - Acesse `http://localhost:8080/login`.
   - Faça login com `carol.sturka@lumia.com.br` / `senha123`. Note o redirecionamento automático para `/colaborador`.
   - Saia e faça login com `zosia.hivemind@lumia.com.br` / `senha123`. Note o redirecionamento automático para `/rh`.

2. **UC02 – Consultar Base de Conhecimento (Colaborador):**
   - No portal do colaborador, localize o bloco **"Base de Conhecimento"** e clique em **"VER TUDO"**.
   - Filtre por categoria ou pesquise na barra de busca. Clique em um artigo (ex.: *Orientações sobre Agendamento de Férias*) para ler o conteúdo.

3. **UC03 – Abrir Ticket de Solicitação (Colaborador):**
   - Na página inicial do colaborador, clique em um card de categoria (ex.: **Férias**) ou pesquise pelo formulário desejado.
   - Selecione **"Agendamento de Férias"**.
   - Preencha a data de início, quantidade de dias, se deseja adiantamento do 13º e anexe um arquivo se solicitado.
   - Clique em **"Submeter Solicitação"**. O ticket é gerado com código `REQ...` e direcionado com status **Pendente** para o gestor.

4. **UC04 – Acompanhar Tickets (Colaborador):**
   - Acesse **"Minhas Solicitações"** no menu ou pelo link **"VER TUDO"**.
   - Filtre pelos botões de status (*Aberto*, *Em andamento*, *Pendente*, *Concluído*, *Reprovado*).
   - Abra um ticket para consultar o histórico, baixar arquivos anexados e enviar mensagens na linha do tempo.

5. **UC05 – Aprovar/Rejeitar Solicitação (Gestor Imediato):**
   - Faça login como a gestora **Helen Sturka** (`helen.sturka@lumia.com.br` / `senha123`).
   - Observe a bolinha vermelha de notificação no menu superior **"Minhas Aprovações"**.
   - Clique em **"Minhas Aprovações"**, selecione o chamado pendente da liderada Carol Sturka, analise as respostas e escolha **Aprovar** (o ticket transita para **Aberto** na fila do RH) ou **Rejeitar** com justificativa (o ticket é encerrado com status **Reprovado**).

6. **UC06 – Atender Ticket (RH):**
   - Faça login como **Zosia Hivemind** (`zosia.hivemind@lumia.com.br` / `senha123`).
   - No painel do RH, visualize os 4 cartões de indicadores. Clique em **"Ver Fila"** dos Tickets em Aberto.
   - Clique em **"Atribuir a Mim"** em um chamado. O ticket passa para **Em Andamento** e entra na seção **"Meu Trabalho"**.
   - Abra o chamado, registre uma devolutiva oficial e altere o status para **Concluído** quando a demanda for finalizada.

7. **UC07 – Gerenciar Formulários (RH):**
   - Na faixa verde **"Gerenciar"**, clique em **"Formulários"**.
   - Crie um novo formulário (nome, descrição, categoria, se requer aprovação de gestor).
   - No **Construtor de Campos**, adicione perguntas de texto, data, número, seleção ou anexo.

8. **UC08 – Cadastrar Usuários com Senha Temporária (RH):**
   - Na faixa **"Gerenciar"**, clique em **"Usuários"** e depois em **"+ Novo Usuário"**.
   - Preencha nome, e-mail, cargo e selecione o gestor imediato.
   - Ao salvar, o sistema gera automaticamente uma **senha aleatória temporária de 10 caracteres** e a exibe em um painel destacado com botão para copiar.

9. **UC09 – Gerenciar Usuários (RH):**
   - Na lista de usuários, edite cargos/hierarquias, bloqueie acessos ou inative contas de colaboradores desligados.

10. **UC10 – Gerenciar Base de Conhecimento (RH):**
    - Na faixa **"Gerenciar"**, clique em **"Base de Conhecimento"**.
    - Crie novos artigos ou publique/despublique e edite artigos existentes.
