# Bikeshare

API REST de aluguel de bicicletas, feita com Spring Boot 3, Java 21, Spring Data JPA e banco H2 em memória.

## O que o sistema faz

- Cadastro de bicicletas e de clientes.
- Início de um aluguel (cliente + bicicleta) e devolução da bicicleta, com cálculo do valor a pagar.
- Envio de recibo por e-mail ao final do aluguel (simulado em log).

## Endpoints

| Método | Rota | Descrição |
|---|---|---|
| POST | `/bikes` | Cadastra uma bicicleta |
| GET | `/bikes` | Lista as bicicletas |
| POST | `/bikes/{id}/maintenance` | Envia a bicicleta para manutenção |
| POST | `/customers` | Cadastra um cliente |
| GET | `/customers/{id}` | Consulta um cliente |
| POST | `/rentals` | Inicia um aluguel |
| POST | `/rentals/{id}/return` | Devolve a bicicleta e finaliza o aluguel |
| GET | `/rentals/{id}` | Consulta um aluguel |

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
