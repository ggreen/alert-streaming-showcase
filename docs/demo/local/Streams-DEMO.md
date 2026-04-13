


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

```shell
Start Alarm App


```shell
java -jar applications/alert-app/target/alert-app-0.0.1-SNAPSHOT.jar --spring.rabbitmq.host=localhost --spring.rabbitmq.username=guest --spring.rabbitmq.password=guest    --server.port=8082
```

```shell
open http://localhost:8082
```

# Partitioning Scalability



# Filtering



Artificial Intelligence
