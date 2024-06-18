# desafio

## Rodando a aplicação
A aplicação conta com um docker-file e um docker-compose para que seja possível subi-lá sem muita dificuldade

**1** - Acessar a pasta raiz do projeto e rodar o comando `mvn clean package`

**2** - Rodar o comando `docker build -t desafio .`

**3** - Ainda na pasta raiz do projeto rodar o comando `docker compose up -d`, para que o ambiente necessário seja disponibilizado

**4** - Executar o comando `docker run -p 8080:8080 desafio` que subirá um container com a aplicação

## Entregáveis
- CRUD de tarefas
  O crud conta ainda com um endpoint para que sejam anexados arquivos relacionados as tarefas, disponível no path `/api/v1/file-upload/{id-tarefa}`, o intuíto era deixar o upload junto com a request de criar e atualizar as tarefas mas o @RequestPart não estava funcionando como o esperado, sempre retornando `415 media type not supported`

- Integração com o S3
  Quando uma tarefa tem um arquivo anexado o mesmo é armazenado em um bucket no S3, o nome do arquivo é o id da tarefa.

- Segurança
  Foi implementada utilizando o spring-security-oauth2 juntamente com o github, todos os endpoints estão seguros, sendo acessíveis apenas para usuários autenticados.

  Caso tenham problemas para fazer a autenticação via Postman, é possível fazer a autenticação pelo navegador, abrir o console de desenvolvedor do navegador e navegar até a aba `network`, efetuar uma requisição para o endpoint `/api/v1/tasks`, selecionar a requisição, e em Headers copiar a chave `Cookie` e o respectivo valor `SESSION=...`. No postman, definir um header com a mesma chave e valor, assim será disponível utilizar os endpoints.

- Swagger
  Para a documentação foi utilizado o Swagger, que está dísponivel no caminho `http://localhost:8080/webjars/swagger-ui/index.html`

  - Testes
    Foram escritos testes unitários e testes de integração. Os testes de integração estão com problema de configuração mas acredito que o código esteja correto, o problema se dá pela utilização da biblioteca TestContainers, acredito que com mais tempo eu seria capaz de configurar tudo corretamente, mas tive alguns contratempos. Os deixei presentes no código caso queiram avaliar. Não tive tempo para escrever os testes de segurança

- Lambda
  Devido a esses contratempo não consegui implementar uma lambda para consumir eventos quando uma tarefa fosse criada.

- Arquivo de testes
  O arquivo de testes se encontra na pasta `/resources/static`
