Comandos Para inicialização do Kafka no Windows

Caminho da instalação: C:\kafka

Inicialização do Zookeper:
 
# .\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties

Inicialização do Kafka

# .\bin\windows\kafka-server-start.bat .\config\server.properties

Visualizar topicos e particoes

# .\bin\windows\kafka-topics.bat --bootstrap-server localhost:9092 --describe

Alterar numero de partições no arquivo server.properties para os novos tópicos iniciarem com a quantidade de tópicos setada.
Para tópicos existentes, executar o comando abaixo:

# .\bin\windows\kafka-topics.bat --alter --bootstrap-server localhost:9092 --topic ECOMMERCE_NEW_ORDER --partitions 3

Visualizar número de mensagens consumidas em cada partição de um tópico:

#  .\bin\windows\kafka-consumer-groups.bat --all-groups --bootstrap-server localhost:9092 --describe