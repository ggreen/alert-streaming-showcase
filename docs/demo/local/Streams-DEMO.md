# High Throughput  

```shell
java -jar applications/perftest/target/perftest-0.0.1-SNAPSHOT.jar  --payload='{"id" : "perf","activity" : "test"}' --messageCount=17000000
```


# Replay

Start Alarm App


```shell
java -jar applications/alert-app/target/alert-app-0.0.1-SNAPSHOT.jar --spring.rabbitmq.host=localhost --spring.rabbitmq.username=guest --spring.rabbitmq.password=guest    --server.port=8080
```

Start Source App

```shell
java -jar applications/alert-source/target/alert-source-0.0.1-SNAPSHOT.jar --spring.profiles.active="mqtt" --csv.file="file://$PWD/applications/alert-source/src/main/resources/csv/alerts.csv"
```


```shell
open http://localhost:8080
```

Restart Apps

------------------------

# Large Fanout


Start another instance of the alarm application

Start Alarm App


```shell
java -jar applications/alert-app/target/alert-app-0.0.1-SNAPSHOT.jar --spring.rabbitmq.host=localhost --spring.rabbitmq.username=guest --spring.rabbitmq.password=guest    --server.port=8082
```

```shell
open http://localhost:8082
```


# Filtering


1. Start Alarm app for all imani alerts

```shell
java -jar applications/alert-app/target/alert-app-0.0.1-SNAPSHOT.jar --spring.rabbitmq.host=localhost --spring.rabbitmq.username=guest --spring.rabbitmq.password=guest --spring.cloud.stream.bindings.input.destination="amq.topic" --stream.destination="alerts.alert" --stream.exchange.bind.key="#"   --stream.filter.sql="account = 'imani' AND level IN ('critical', 'high','medium','low')" --server.port=8083 --stream.activity.filter.name="account" --stream.activity.filter.value="imani" --alert.refresh.rateSeconds=1
```


```shell
open http://localhost:8083
```

2. Start app CRITICAL ONLY alerts

```shell
java -jar applications/alert-app/target/alert-app-0.0.1-SNAPSHOT.jar --spring.rabbitmq.host=localhost --spring.application.name="imani-critical" --spring.rabbitmq.username=guest --spring.rabbitmq.password=guest --spring.cloud.stream.bindings.input.destination="amq.topic" --stream.destination="alerts.alert" --stream.exchange.bind.key="#"   --stream.filter.sql="account = 'imani' AND level IN ('critical')" --server.port=8084 --stream.activity.filter.name="account" --stream.activity.filter.value="imani" --alert.refresh.rateSeconds=1
```

```shell
open http://localhost:8084
```


3. Posting additional alerts

```shell
java -jar applications/alert-source/target/alert-source-0.0.1-SNAPSHOT.jar --spring.profiles.active="mqtt" --csv.file="file://$PWD/applications/alert-source/src/main/resources/csv/alerts-filtering.csv"
```


# Partitioning Scalability


Artificial Intelligence