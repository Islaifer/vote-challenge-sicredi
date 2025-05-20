
# Gerenciador de Votação

Este projeto tem o intuito de fazer um gerenciador de pautas e votação para o desafio passado pela empresa Sicredi.

Este projeto pode rodar em containers docker ou nativamente, de acordo com as preferências de quem quiser rodá-lo.




## Instalação e Uso

### Requisitos
Os requisitos para a instalação e utilização desse projeto são:
- docker e docker compose
- java 21
- gradle 8.5 ou superior

Se for rodar fora de docker, serão necessários os requisitos:
- kafka
- mariadb

### Como instalar
Primeiro clone este repositório com

```bash
  git clone https://github.com/Islaifer/vote-challenge-sicredi.git
```

Após isso, vá até o diretório que o projeto foi clonado.

(Opcional)
Na pasta raíz do projeto possui o arquivo de docker-compose (docker-compose.yml), nele será possível fazer as modificações de acordo com suas necessidades, como para onde vão os logs por exemplo. Para alterar a pasta de log de cada módulo do projeto, modifique sua linha

```bash
  volumes:
      - /tmp/logs/vote-challenge:/logs
```

Onde a primeira parte dos : seria o destino do log deste modulo.

Nota: se deseja alterar usuário e senha do mariadb, não esqueça de mudar também no "DB_USERNAME e DB_PASSWORD" da sessão de environment de cada módulo no arquivo. O mesmo vale com o database, o nome da url do Kafka e as portas.

Com isso, basta apenas executar o script de build and start para buildar os projetos e rodar via docker.

#### Para Windows

Utilize o power shell e execute o script build-and-start.ps1 com:

```bash
  .\build-and-start.ps1
```
Certifique-se de que o diretório esteja em um caminho onde as pastas correspondentes NÃO tenham nenhum espaço ou acento no nome.  

Nota para usuários de windows:
Caso seu sistema de política de execução do power shell seja Restricted, então será necessário mudar temporariamente para executar o script. Para fazer a alteração temporária, utilize:

```bash
  Set-ExecutionPolicy RemoteSigned -Scope Process para poder rodar
```

#### Para Linux

Dê permissão de execução ao script e o execute:

```bash
  chmod +x build-and-start.sh
  ./build-and-start.sh
```

#### Nota: Tanto para Windows quanto para Linux, NÃO execute o script de build e run como root (ou adm), quando for rodar o docker dentro do script ele pedirá a permissão.

### Rodando sem docker
Para rodar sem docker, primeiro configure as variáveis de ambiente:
- DB_URL com o host para o banco de dados (ex: localhost)
- DB_PORT com a porta do seu banco de dados
- DB_USERNAME com o nome do usuario de banco de dados
- DB_PASSWORD com a senha do usuario de banco de dados
- KAFKA_URL com o host do seu kafka (ex: localhost)

Suba o kafka na porta 9092 e o mariadb.

Utilize o gradlew (gradlew.bat no Windows) com as tarefas de clean build para buildar os módulos: vote-challenge, agenda-processor e vote-visualizer.

Depois de buildar cada projeto, vá em /build/libis de cada projeto e execute o jar, como por exemplo:

```bash
  java -jar vote-challenge-1.0.0.jar
```

Você também pode executar os modulos dentro de uma IDE desejada, só não esqueça de configurar as variáveis de ambiente e subir tanto o kafka na porta 9092 quanto o mariadb.

## Uso
Após isso, sempre que quiser executá-lo por docker pode apenas utilizar o script de build and start.

Com os projetos em execução, poderemos utilizar as apis pela porta 8080 e ver os resultados das pautas fechadas pela porta 8082. Ou seja:
- localhost:8080 ficam as chamadas de apis
- localhost:8082 possui uma simples pagina html que mostra todas as pautas expiradas e seus resultados.
## Documentação da API

A api implementa o swagger, e pode ser acessado pela rota /swagger-ui/index.html, ou seja, após rodar a aplicação, poderá acessar:
localhost:8080/swagger-ui/index.html

Neste local, conseguirá ver a documentação da api e poder experimentá-las também.



## Documentação

A aplicação possui 3 módulos que se comunicam através de mensageria kafka.
O kafka foi escolhido pela sua performance e fácil usábilidade, além de poder ser usado facilmente via docker.

Os módulos vote-challenge e agenda-processor utilizam de conexão com o mariadb.
O mariadb foi escolhido pela preferência de uso.

Todos os módulos utilizam o framework do spring boot 3.4.5 e java 21, escolhidos pela facilidade de criação de apis e utilização do kafka.

#### vote-challenge
Este módulo possui as chamadas de api designadas a criação de associados, criação e abertura de pautas e sistema de votação.
Sempre que uma pauta é aberta, ele manda uma mensagem para o módulo agenda-processor, esse por sua vez vai programar o fechamento da pauta de acordo com a expiração estabelecida na abertura da pauta.

#### agenda-processor
Este módulo programa o fechamento e fecha as pautas expiradas. Sempre que ocorre um fechamento de pauta ele envia uma mensagem para o módulo de vote-visualizer.
Ele possui um carregamento inicial que verifica todas as pautas abertas, fecha se estiverem expiradas e programa o fechamento se ainda não expiraram. Isso evita que uma pauta fique aberta eternamente pelo encerramento do módulo ou de todo o projeto.

#### vote-visualizer
Este módulo serve para ver todas as pautas que são fechadas. Ele recebe os eventos vindo do kafka e exibe na tela simples de html a pauta, os votos a favor e os votos contra, e sinaliza o vencedor.
Para sincronizar com a tela html, o módulo estabelece uma conexão SSE pelo endpoint /result-stream.

