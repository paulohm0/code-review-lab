# Task 002 – Vetclinic

| | |
|---|---|
| **Dificuldade** | FÁCIL |
| **Tamanho** | Pequeno (~35 arquivos, ~25 classes) |
| **Domínio** | Clínica veterinária (agendamento de consultas) |
| **Temas** | Separação de responsabilidades e DTOs/Mappers; CRUD e camadas |
| **Tipo de task** | REFATORAÇÃO |
| **Defeitos extras a encontrar** | 6 |

## Ticket #517: tirar as regras de agendamento de dentro do controller

**Contexto:** o módulo de consultas nasceu como um protótipo e hoje o `AppointmentController` faz quase tudo: carrega as entidades, aplica as regras de horário, de conflito e de cobrança, altera o estado da consulta, dispara as notificações e ainda monta o JSON de resposta à mão. O time quer incluir novas regras de cobrança no próximo mês e ninguém se sente seguro de mexer nisso do jeito que está. Os módulos de tutores e pets são a referência de como o restante do projeto deve ficar.

**O que se espera:** reorganizar o módulo de consultas seguindo a mesma divisão de camadas do restante do projeto, sem mudar o que a API faz para os clientes.

### Critérios de aceite

1. O `AppointmentController` fica apenas com responsabilidades HTTP: receber a requisição, delegar e devolver a resposta.
2. As regras de negócio (horário de funcionamento, conflito de agenda, cálculo do valor, cancelamento, conclusão) e o disparo das notificações saem do controller e passam a viver na camada adequada.
3. O controller deixa de montar `Map<String, Object>` na mão: as respostas usam um DTO próprio, com a conversão em um lugar só.
4. O contrato JSON das respostas não muda (mesmos campos e nomes) e as rotas continuam as mesmas.
5. Os testes existentes continuam passando sem alterar suas asserções.
6. As regras ficam cobertas por testes próprios, que não dependam de subir a API inteira.

### Restrições

- Não adicionar dependências novas ao `pom.xml`.
- Manter o diff focado no módulo de consultas e no que for necessário para ele.
- As regras de negócio documentadas no `README.md` devem continuar valendo.

## Instruções de trabalho

- Leia o código como num PR e trabalhe **direto nos arquivos**: resolva o ticket e, ao longo do caminho, corrija o que considerar erro e/ou deixe comentários `// REVIEW: <o que vi e por quê>` (sugestão de correção é bem-vinda, mas opcional).
- Não é preciso preencher nenhum relatório. O tech lead está online no chat para dúvidas.
- Quando terminar, abra um PR da branch de solução para o `main` e avise no chat.
