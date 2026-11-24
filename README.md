# Baozi Store API

API REST da **Baozi Store**, uma loja que vende pão chinês, feita para a Atividade Prática de **Desenvolvimento Web Back-End** (Uninter).
Ela cadastra clientes e produtos e registra pedidos simples, em que um cliente compra um único produto em determinada quantidade.

## Tecnologias

- Java 21
- Spring Boot 4.2 (Spring Web MVC)
- Spring Data JPA / Hibernate
- Banco relacional **H2**, gravado em arquivo (`./data/baozistore.mv.db`)
- Bean Validation
- Maven (via Maven Wrapper, não precisa instalar)

## Estrutura (MVC)

```
src/main/java/br/com/baozistore
├── BaoziStoreApplication.java      # classe principal
├── model/                          # entidades JPA
│   ├── Cliente.java
│   ├── Produto.java
│   ├── Pedido.java
│   └── PedidoRequest.java          # JSON de entrada do pedido (clienteId, produtoId, quantidade)
├── repository/                     # interfaces Spring Data JPA
│   ├── ClienteRepository.java
│   ├── ProdutoRepository.java
│   └── PedidoRepository.java
└── controller/                     # endpoints REST
    ├── ClienteController.java
    ├── ProdutoController.java
    ├── PedidoController.java
    └── TratadorDeErros.java        # respostas de erro em JSON
```

## Entidades

| Entidade | Campo | Tipo | Observação |
|---|---|---|---|
| **Cliente** | id | Long | gerado automaticamente |
| | nome | String | obrigatório |
| | clienteDesde | LocalDate | formato `AAAA-MM-DD`; se não for informado, usa a data de hoje |
| **Produto** | id | Long | gerado automaticamente |
| | nome | String | obrigatório |
| | preco | BigDecimal | obrigatório, maior que zero |
| | estoque | Boolean | `true` = disponível em estoque |
| **Pedido** | id | Long | gerado automaticamente |
| | clienteId | Long | chave estrangeira para Cliente (`cliente_id`) |
| | produtoId | Long | chave estrangeira para Produto (`produto_id`) |
| | quantidade | Integer | obrigatório, maior que zero |

## Endpoints

URL base: `http://localhost:8080`

| Método | Rota | Descrição | Sucesso |
|---|---|---|---|
| POST | `/clientes` | Cadastra cliente | 201 |
| GET | `/clientes` | Lista todos os clientes | 200 |
| GET | `/clientes/{id}` | Consulta cliente por ID | 200 |
| PUT | `/clientes/{id}` | Atualiza cliente (opcional) | 200 |
| DELETE | `/clientes/{id}` | Apaga cliente | 204 |
| POST | `/produtos` | Cadastra produto | 201 |
| GET | `/produtos` | Lista todos os produtos | 200 |
| GET | `/produtos/{id}` | Consulta produto por ID | 200 |
| PUT | `/produtos/{id}` | Atualiza produto (opcional) | 200 |
| DELETE | `/produtos/{id}` | Apaga produto | 204 |
| POST | `/pedidos` | Registra pedido | 201 |
| GET | `/pedidos` | Lista todos os pedidos | 200 |
| GET | `/pedidos/{id}` | Consulta pedido por ID | 200 |
| PUT | `/pedidos/{id}` | Atualiza pedido (opcional) | 200 |
| DELETE | `/pedidos/{id}` | Apaga pedido | 204 |

Códigos de erro: **400** para dados inválidos ou cliente/produto inexistente no pedido, **404** para ID não encontrado e **409** ao apagar um cliente ou produto que ainda tem pedidos.

### Exemplos de JSON

```http
POST /clientes
{ "nome": "Fulano1234567", "clienteDesde": "2026-11-24" }

POST /produtos
{ "nome": "Baozi de Porco", "preco": 8.50, "estoque": true }

POST /pedidos
{ "clienteId": 1, "produtoId": 1, "quantidade": 6 }
```

Resposta do `GET /pedidos/1`:

```json
{
  "id": 1,
  "cliente": { "id": 1, "nome": "Fulano1234567", "clienteDesde": "2026-11-24" },
  "produto": { "id": 1, "nome": "Baozi de Porco", "preco": 8.50, "estoque": true },
  "quantidade": 6
}
```

## Como executar

Pré-requisito: JDK 21 com `JAVA_HOME` configurado.

```powershell
.\mvnw.cmd spring-boot:run
```

A API sobe em `http://localhost:8080`.

**Console do banco H2:** `http://localhost:8080/h2-console`
JDBC URL `jdbc:h2:file:./data/baozistore`, usuário `sa`, senha em branco.

Para rodar os testes: `.\mvnw.cmd test`

## Postman

Importe `postman/BaoziStore.postman_collection.json` no Postman. Na aba **Variables** da coleção, troque `nomeCliente` pelo seu **Nome+RU**.
Para apagar um cliente ou produto, apague antes o pedido que usa ele.
