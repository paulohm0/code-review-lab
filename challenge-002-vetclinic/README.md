# Vetclinic

API REST de agendamento de consultas de uma clínica veterinária, feita com Spring Boot 3, Java 21, Spring Data JPA e banco H2 em memória.

## O que o sistema faz

- Cadastro de tutores e de seus pets.
- Consulta de veterinários (dois já vêm cadastrados ao subir a aplicação).
- Agendamento, cancelamento e conclusão de consultas, com cálculo do valor cobrado.
- Aviso por e-mail ao tutor a cada mudança na consulta (simulado em log).

## Regras de negócio

- Consultas eletivas são agendadas entre 08:00 e 18:00; emergências podem ser em qualquer horário e custam 50% a mais.
- Um veterinário não pode ter duas consultas no mesmo horário.
- Cancelar com 24h ou mais de antecedência não gera cobrança; com menos, cobra-se metade do valor.
- Só consultas agendadas podem ser concluídas.

## Endpoints

| Método | Rota | Descrição |
|---|---|---|
| POST | `/tutors` | Cadastra um tutor |
| GET | `/tutors` | Lista os tutores |
| GET | `/tutors/{id}` | Consulta um tutor |
| POST | `/pets` | Cadastra um pet |
| GET | `/pets/{id}` | Consulta um pet |
| GET | `/pets?tutorId=` | Lista os pets de um tutor |
| GET | `/veterinarians` | Lista os veterinários |
| POST | `/appointments` | Agenda uma consulta |
| GET | `/appointments/{id}` | Consulta uma consulta |
| GET | `/appointments?veterinarianId=&date=` | Agenda de um veterinário em um dia |
| POST | `/appointments/{id}/cancel` | Cancela a consulta |
| POST | `/appointments/{id}/complete?notes=` | Conclui a consulta |

O arquivo `requests.http` tem exemplos de chamadas.

## Como rodar

Não precisa ter o Maven instalado, o projeto usa o Maven Wrapper (a primeira execução baixa o Maven).

```bash
./mvnw spring-boot:run      # Linux/macOS/Git Bash
mvnw.cmd spring-boot:run    # Windows (cmd/PowerShell)
```

A API sobe em `http://localhost:8080`.

## Como rodar os testes

```bash
./mvnw verify
```
