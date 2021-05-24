Avaliação técnica back-end V1

Tecnologias utilizadas: Micronaut, Java 11, JPA, Junit 5 e banco de dados embarcado H2.

Para iniciar os serviços por linha de comando e digitar mvnw mn:run

Os testes podem ser iniciados ao digitar mvnw test

O debug do projeto pode ser feito utilizando mvnw mn:run -Dmn.debug
Através da IDE Eclipse é possivel conectar por "Remote Java Application" na porta 5005.

Criei testes unitários apenas do endpoint principal que é o SicrediServiceEndpoint, nele estão os micro serviços solicitados na avaliação.

Também criei testes utilizando o Postman para facilitar o debug do código, a coleção esta na raiz do projeto com o nome Sicredi.postman_collection.json.

Optei por fazer contabilização dos votos logo após o fim do tempo estabelecido da sessão.

Fiz a tarefa bonus 1 mas infelizmente não tive tempo de fazer a segunda. Fiz o teste de performance utilizando o próprio Runner do Postman e acredito que esta bastante satisfatório.

Para o versionamento do código eu utilizo o fluxo do Gitflow!

Achei a avaliação muito interessante. Agradeço a oportunidade e aguardo retorno!
