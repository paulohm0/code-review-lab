# Task 001 – Bikeshare

| | |
|---|---|
| **Dificuldade** | FÁCIL |
| **Tamanho** | Pequeno (~20 arquivos) |
| **Domínio** | Aluguel de bicicletas |
| **Temas** | CRUD e camadas; validação e tratamento de erros |
| **Tipo de task** | PROBLEMA |
| **Defeitos extras a encontrar** | 5 (além da causa do problema abaixo) |

## Chamado #482: cliente não é cobrado corretamente pelo aluguel

**Relato do financeiro:** ao conferir o fechamento do dia, vimos aluguéis de curta duração com cobrança de **R$ 0,00**, e alguns aluguéis de mais de uma hora com valor menor do que o esperado. O caixa está fechando com diferença.

**Regra de negócio:** o valor é de **R$ 5,00 por hora**, e qualquer fração de hora conta como uma hora inteira. Ou seja, toda devolução cobra no mínimo R$ 5,00 (ex.: 10 min → R$ 5,00; 1h05 → R$ 10,00).

**Comportamento esperado:** o valor de `totalPrice` retornado na devolução respeita a regra acima.

**Comportamento observado:** devolvendo a bicicleta logo depois de retirá-la, o `totalPrice` volta `0.0`.

**Como reproduzir:**
1. Suba a aplicação (veja o `README.md`).
2. Use o `requests.http`: cadastre uma bicicleta e um cliente, inicie um aluguel e devolva a bicicleta em seguida.
3. Observe o `totalPrice` na resposta da devolução.

## Instruções de trabalho

- Leia o código como num PR e trabalhe **direto nos arquivos**: resolva o chamado e, ao longo do caminho, corrija o que considerar erro e/ou deixe comentários `// REVIEW: <o que vi e por quê>` (sugestão de correção é bem-vinda, mas opcional).
- Não é preciso preencher nenhum relatório. O tech lead está online no chat para dúvidas.
- Quando terminar, abra um PR da branch de solução para o `main` e avise no chat.
