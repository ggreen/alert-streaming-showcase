# High Throughput  

1. Run PerfTest

```shell
cd /Users/Projects/solutions/integration/event-streaming/dev/alert-event-streaming-showcase
java -jar applications/perftest/target/perftest-0.0.1-SNAPSHOT.jar  --payload='{"id" : "perf","activity" : "test"}' --messageCount=17000000
```

---------------------------

# Replay

Start Alarm App


```shell
cd /Users/Projects/solutions/integration/event-streaming/dev/alert-event-streaming-showcase
java -jar applications/alert-app/target/alert-app-0.0.1-SNAPSHOT.jar --spring.rabbitmq.host=localhost --spring.rabbitmq.username=guest --spring.rabbitmq.password=guest    --server.port=8080
```


```shell
open http://localhost:8080
```

Start Source App

```shell
cd /Users/Projects/solutions/integration/event-streaming/dev/alert-event-streaming-showcase
java -jar applications/alert-source/target/alert-source-0.0.1-SNAPSHOT.jar --spring.profiles.active="mqtt" --csv.file="file://$PWD/applications/alert-source/src/main/resources/csv/alerts.csv" 
```



Restart Apps

------------------------

# Large Fanout


Start another instance of the alarm application

Start Alarm App


```shell
cd /Users/Projects/solutions/integration/event-streaming/dev/alert-event-streaming-showcase
java -jar applications/alert-app/target/alert-app-0.0.1-SNAPSHOT.jar --spring.rabbitmq.host=localhost --spring.rabbitmq.username=guest --spring.rabbitmq.password=guest    --server.port=8082
```

```shell
open http://localhost:8082
```


# Filtering


1. Start Alarm app for all imani alerts

```shell
cd /Users/Projects/solutions/integration/event-streaming/dev/alert-event-streaming-showcase
java -jar applications/alert-app/target/alert-app-0.0.1-SNAPSHOT.jar --spring.rabbitmq.host=localhost --spring.rabbitmq.username=guest --spring.rabbitmq.password=guest --spring.cloud.stream.bindings.input.destination="amq.topic" --stream.destination="alerts.alert" --stream.exchange.bind.key="#"   --stream.filter.sql="account = 'imani' AND level IN ('critical', 'high','medium','low')" --server.port=8083 --stream.activity.filter.name="account" --stream.activity.filter.value="imani" --alert.refresh.rateSeconds=1
```


```shell
open http://localhost:8083
```

2. Start app CRITICAL ONLY alerts

```shell
cd /Users/Projects/solutions/integration/event-streaming/dev/alert-event-streaming-showcase
java -jar applications/alert-app/target/alert-app-0.0.1-SNAPSHOT.jar --spring.rabbitmq.host=localhost --spring.application.name="imani-critical" --spring.rabbitmq.username=guest --spring.rabbitmq.password=guest --spring.cloud.stream.bindings.input.destination="amq.topic" --stream.destination="alerts.alert" --stream.exchange.bind.key="#"   --stream.filter.sql="account = 'imani' AND level IN ('critical')" --server.port=8084 --stream.activity.filter.name="account" --stream.activity.filter.value="imani" --alert.refresh.rateSeconds=1
```

```shell
open http://localhost:8084
```


3. Posting additional alerts

```shell
cd /Users/Projects/solutions/integration/event-streaming/dev/alert-event-streaming-showcase

java -jar applications/alert-source/target/alert-source-0.0.1-SNAPSHOT.jar --spring.profiles.active="mqtt" --csv.file="file://$PWD/applications/alert-source/src/main/resources/csv/alerts-filtering.csv"
```
-------------------------------

# Artificial Intelligence

Start AI Processor Application


```shell
cd /Users/Projects/solutions/integration/event-streaming/dev/alert-event-streaming-showcase
java -jar applications/alert-ai-processor/target/alert-ai-processor-0.0.1-SNAPSHOT.jar --stream.activity.filter.value=josiah --spring.profiles.active=finance --spring.ai.ollama.chat.options.model=llama3.2:latest --alerts.inference.batch=6
```

Start Activity Source

```shell
java -jar applications/activity-source/target/activity-source-0.0.1-SNAPSHOT.jar --server.port=8555 --csv.file="file://$PWD/applications/activity-source/src/main/resources/csv/activities.csv"
```



```shell
curl -X 'POST' \
  'http://localhost:8555/publisher?topic=josiah' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{ "id" : "70", "icon" : "fa-credit-card", "account" : "josiah", "time" : "07:15 PM", "activity" : "type: SALE, pan: 4111XXXXXX1111, amount: 100.00, date: 1-7-2026 19:12:34 terminal_id: TERM_88291, merchant_id: MERCH_55432" }'

curl -X 'POST' \
  'http://localhost:8555/publisher?topic=josiah' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{ "id" : "71", "icon" : "fa-credit-card",  "account" : "josiah", "time" :  "07:17 PM", "activity" : "type: SALE, pan: 4111XXXXXX1111, amount: 1000.01, date: ''1-7-2026 19:17:32'' terminal_id: TERM_88291, merchant_id: MERCH_5555" }'

curl -X 'POST' \
  'http://localhost:8555/publisher?topic=josiah' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{ "id" : "72", "icon" : "fa-temperature-low",  "account" : "josiah", "time" : "07:19 PM", "activity" : "type: SALE, pan: 4111XXXXXX1111, amount: 1.01, date: ''1-7-2026 19:19:32'' terminal_id: TERM_88291, merchant_id: MERCH_55432"}' 
   
curl -X 'POST' \
  'http://localhost:8555/publisher?topic=josiah' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{ "id" : "73", "icon" : "fa-credit-card",  "account" : "josiah", "time" : "07:17 PM", "activity" : "type: SALE, pan: 4111XXXXXX1111, amount: 0.01, date: ''1-7-2026 19:17:32'' terminal_id: TERM_88291, merchant_id: MERCH_55432" }'
  
curl -X 'POST' \
  'http://localhost:8555/publisher?topic=josiah' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{ "id" : "74", "icon" : "fa-credit-card",  "account" : "josiah", "time" : "07:19 PM", "activity" : "type: SALE, pan: 4111XXXXXX1111, amount: 1.01, date: ''1-7-2026 19:19:32'' terminal_id: TERM_88291, merchant_id: MERCH_55432" }'
  
curl -X 'POST' \
  'http://localhost:8555/publisher?topic=josiah' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{ "id" : "75", "icon" : "fa-credit-card",  "account" : "josiah", "time" :  "07:20 PM", "activity" : "type: SALE, pan: 4111XXXXXX1111, amount: 5.01, date: ''1-7-2026 19:20:32'' terminal_id: TERM_88291, merchant_id: MERCH_5555" }'

curl -X 'POST' \
  'http://localhost:8555/publisher?topic=josiah' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{ "id" : "76", "icon" : "fa-credit-card",  "account" : "josiah", "time" : "07:15 PM", "activity" : "type: SALE, pan: 4111XXXXXX1111, amount: 100.00, date: ''1-7-2026 19:12:34'' terminal_id: TERM_88291, merchant_id: MERCH_55432" }'
```


Start Josiah Alert App


```shell
java -jar applications/alert-app/target/alert-app-0.0.1-SNAPSHOT.jar --spring.rabbitmq.host=localhost --spring.rabbitmq.username=guest --spring.rabbitmq.password=guest --spring.cloud.stream.bindings.input.destination="amq.topic" --stream.destination="alerts.alert" --stream.exchange.bind.key="#"   --stream.filter.sql="account = 'josiah' AND level IN ('critical', 'high','medium','low')" --server.port=8777 --stream.activity.filter.name="account" --stream.activity.filter.value="josiah" --alert.refresh.rateSeconds=1
```


```shell
open http://localhost:8777
```




