# Projeto de APIs REST para Gestão de Clientes e Transferências

## Descrição

Este projeto expõe APIs no padrão REST (JSON) para gerenciar clientes e realizar transferências bancárias. A aplicação foi desenvolvida em Java 17, usando Spring Boot e um banco de dados em memória H2. O projeto atende aos seguintes requisitos:

1. Cadastrar um cliente com ID único, nome, número da conta (único) e saldo em conta.
2. Listar todos os clientes cadastrados.
3. Buscar um cliente pelo número da conta.
4. Realizar transferência entre duas contas, garantindo saldo suficiente e limite de transferência de R$ 100,00.
5. Buscar transferências relacionadas a uma conta, em ordem decrescente de data, incluindo transferências sem sucesso.

## Funcionalidades

### Endpoints

1. **Cadastrar Cliente**

   - **Endpoint:** `POST /api/v1/clientes`
   - **Request Body:**
     ```json
     {
       "nome": "Ana",
       "numeroConta": "1234",
       "saldo": 500.0
     }
     ```
   - **Response:**
     - `201 Created` em caso de sucesso.
     - `404 Not found` se o número da conta já existir.
     - `400 Bad Request` para erros de validação.

2. **Listar Todos os Clientes**

   - **Endpoint:** `GET /api/v1/clientes`
   - **Response:**
     - `200 OK` com a lista de clientes.

3. **Buscar Cliente pelo Número da Conta**

   - **Endpoint:** `GET /api/v1/clientes/{numeroConta}`
   - **Response:**
     - `200 OK` com os dados do cliente.
     - `400 Bad Request` se o cliente não for encontrado.

4. **Realizar Transferência**

   - **Endpoint:** `POST /api/v1/transferencias`
   - **Request Body:**
     ```json
     {
       "contaOrigem": "1234",
       "contaDestino": "6543",
       "valor": 50.0
     }
     ```
   - **Response:**
     - `200 OK` em caso de sucesso.
     - `400 Bad Request` para saldo insuficiente ou valor acima do limite permitido.
     - `400 Bad Request` se a conta de origem ou destino não for encontrada.

5. **Buscar Transferências de uma Conta**

   - **Endpoint:** `GET /api/v1/transferencias/{numeroConta}`
   - **Response:**
     - `200 OK` com a lista de transferências.

## Requisitos

- Java 11 ou superior
- Maven ou Gradle
- Banco de dados em memória (H2)

## Configuração e Execução

### Pré-requisitos

- JDK 17
- Maven

### Configuração

1. Clone o repositório:
   ```sh
   git clone [https://github.com/anazucchelli/api-clientes.git

2. Compile o projeto com Maven:
  `mvn clean install`

3. Execute a aplicação:
  `http://localhost:8080`

## Controle de Versão da API
A versão atual da API é v1. A versão é incluída nos endpoints como parte do caminho, por exemplo, /api/v1/clientes.

## Tratamento de Erros
A aplicação utiliza códigos de resposta HTTP apropriados para indicar o sucesso ou falha das operações:

200 OK: Operação bem-sucedida.
201 Created: Recurso criado com sucesso.
400 Bad Request: Requisição inválida.
404 Not Found: Recurso não encontrado.

## Documentação
   Uma vez que o projeto esteja rodando é possível acessar toda a documentação da API nesse link: 
   `http://localhost:8080/swagger-ui.html`

![image](https://github.com/anazucchelli/api-clientes/assets/39284021/8711160f-fabf-4a00-ad3b-ebdfeb917433)
