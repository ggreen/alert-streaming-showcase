# Payments Alert SQL Demo

0. Disable WSS Agent!!!!


Start Ollama

```shell
ollama serve
```


1. Start Rabbit

```shell
./deployment/local/containers/rabbit.sh
```

2. Start MQTT 5 Source

```shell
java -jar applications/http-source/target/http-source-0.0.1-SNAPSHOT.jar \
  --mqtt.connectionUrl=tcp://localhost:1883 \
  --spring.application.name=http-mqtt-source \
  --mqtt.userName=guest \
  --mqtt.userPassword=guest --spring.profiles.active=mqtt
```

3. Start Alarm app for all imani alerts

```shell
java -jar applications/alert-app/target/alert-app-0.0.1-SNAPSHOT.jar --spring.rabbitmq.host=localhost --spring.rabbitmq.username=guest --spring.rabbitmq.password=guest --spring.cloud.stream.bindings.input.destination="amq.topic" --stream.destination="alerts.alert" --stream.exchange.bind.key="#"   --stream.filter.sql="account = 'imani' AND level IN ('critical', 'high','medium','low')" --server.port=8080 --stream.activity.filter.name="account" --stream.activity.filter.value="imani" --alert.refresh.rateSeconds=1
```


4. Start app CRITICAL ONLY alerts

```shell
java -jar applications/alert-app/target/alert-app-0.0.1-SNAPSHOT.jar --spring.rabbitmq.host=localhost --spring.application.name="imani-critical" --spring.rabbitmq.username=guest --spring.rabbitmq.password=guest --spring.cloud.stream.bindings.input.destination="amq.topic" --stream.destination="alerts.alert" --stream.exchange.bind.key="#"   --stream.filter.sql="account = 'imani' AND level IN ('critical')" --server.port=8911 --stream.activity.filter.name="account" --stream.activity.filter.value="imani" --alert.refresh.rateSeconds=1
```

5. Open RabbitMQ Management Dashboard

```shell
open http://localhost:15672
```


----------------

# Post Alerts

6. Post critical alert

```shell
curl -X 'POST' \
  'http://localhost:8383/publisher?topic=alerts.alert' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -H "account: imani" \
  -H "level: critical" \
  -d '{"id" : "01", "account" : "imani", "level" : "critical", "time" : "5:00AM", "event" : "Multiple ATM pin numbers entries incorrect at location in Jersey City, New Jersey" }'
```

7. Post Alert

```shell
curl -X 'POST' \
  'http://localhost:8383/publisher?topic=alerts' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -H "account: imani" \
  -H "level: high" \
  -d '{"id" : "02", "account" : "imani", "level" : "high", "time" : "6:15AM", "event" : "Account balance just dropped below threshold!" }'
  
```


7. Post Alert

```shell
curl -X 'POST' \
  'http://localhost:8383/publisher?topic=alerts' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -H "account: imani" \
  -H "level: medium" \
  -d '{"id" : "03", "account" : "imani", "level" : "medium", "time" : "7:00AM", "event" : "Account password has not been updated in a long time and is recommended to update as soon as possible!" }'
  
```


6. Post Alert

```shell
curl -X 'POST' \
  'http://localhost:8383/publisher?topic=alerts' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -H "account: imani" \
  -H "level: low" \
  -d '{"id" : "04", "account" : "imani", "level" : "low", "time" : "7:00AM", "event" : "Friendly reminder to contribute to your Roth IRA" }'
```



8. Open Imani for All alerts

```shell
open http:///localhost:8080
```


9. Only Critical

```shell
open http:///localhost:8911
```


-----------------------------


11. Start AMQP 1.0 Source

```shell
java -jar applications/http-source/target/http-source-0.0.1-SNAPSHOT.jar \
  -spring.rabbitmq.host=localhost \
  --spring.application.name=activities-source \
  --spring.rabbitmq.username=guest \
  --spring.rabbitmq.password=guest --spring.profiles.active="amqp1.0" --source.amqp.filter.property.name="account" --server.port=8555 --spring.cloud.stream.bindings.output.destination="activities.activity"
```


11. Publish Activity

```shell
curl -X 'POST' \
  'http://localhost:8555/publisher?topic=imani' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "id" :  "1", 
  "account" : "imani",
  "icon" : "fa-credit-card",
  "time" : "06:30 PM", 
  "activity" : "Opened new credit card account"
}'
curl -X 'POST' \
  'http://localhost:8555/publisher?topic=imani' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "id" :  "2", 
  "account" : "imani",
  "icon" : "fa-file-invoice",
  "time" : "06:31 PM", 
  "activity" : "Account statements shipped to home address"
}'

curl -X 'POST' \
  'http://localhost:8555/publisher?topic=imani' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "id" :  "3", 
  "account" : "imani",
  "icon" : "fa-hand-holding-dollar",
  "time" : "06:33 PM", 
  "activity" : "Wire transfer of amount above threshold"
}'
curl -X 'POST' \
  'http://localhost:8555/publisher?topic=imani' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "id" :  "4", 
  "account" : "imani",
  "icon" : "fa-wallet",
  "time" : "06:31 PM", 
  "activity" : "Visa credit card download to digital wallet"
}'
```


11. Restart Apps for Data as Service

Data as a service

------------------

# PErformance



# Artificial Intelligence

Start Ollama

```shell
ollama run llama3.2:latest
```

The following is an example prompt used for fraud
detection.

```text
Given the following payment activities, identify potential fraud alerts, 
For each alert response with the "level" with values of (CRITICAL, HIGH, MEDIUM, Low),
the time and the event which contains why you believe this is an alert
ONLY RESPONSE the Json Object fields level, time, and event

[ACTIVITIES]
```

```json
[
  { "icon" : "fa-credit-card",  "account" : "josiah", "time" : "07:15 PM", "activity" : "type: SALE, pan: 4111XXXXXX1111, amount: 100.00, date: '1-7-2026 19:12:34' terminal_id: TERM_88291, merchant_id: MERCH_55432"},
  { "icon" : "fa-credit-card",  "account" : "josiah", "time" : "07:17 PM", "activity" : "type: SALE, pan: 4111XXXXXX1111, amount: 1000.01, date: '1-7-2026 19:17:32' terminal_id: TERM_88291, merchant_id: MERCH_5555"},
  { "icon" : "fa-credit-card",  "account" : "josiah", "time" : "07:19 PM", "activity" : "type: SALE, pan: 4111XXXXXX1111, amount: 1.01, date: '1-7-2026 19:19:32' terminal_id: TERM_88291, merchant_id: MERCH_55432"},
  { "icon" : "fa-credit-card",  "account" : "josiah", "time" : "07:17 PM", "activity" : "type: SALE, pan: 4111XXXXXX1111, amount: 0.01, date: '1-7-2026 19:17:32' terminal_id: TERM_88291, merchant_id: MERCH_55432"},
  { "icon" : "fa-credit-card",  "account" : "josiah", "time" : "07:19 PM", "activity" : "type: SALE, pan: 4111XXXXXX1111, amount: 1.01, date: '1-7-2026 19:19:32' terminal_id: TERM_88291, merchant_id: MERCH_55432"},
  { "icon" : "fa-credit-card",  "account" : "josiah", "time" : "07:20 PM", "activity" : "type: SALE, pan: 4111XXXXXX1111, amount: 5.01, date: '1-7-2026 19:20:32' terminal_id: TERM_88291, merchant_id: MERCH_5555"},
  { "icon" : "fa-credit-card",  "account" : "josiah", "time" : "07:15 PM", "activity" : "type: SALE, pan: 4111XXXXXX1111, amount: 100.00, date: '1-7-2026 19:12:34' terminal_id: TERM_88291, merchant_id: MERCH_55432"}
]
```

```text
Use Context Below
CONTEXT:

Activities for the same account from one more than 1 terminal_id within 1 minute time differences is a CRITICAL alert.
Activities for the same account from one more than 1 terminal_id within 5 minute time differences is a HIGH alert.
Activities with a series of very small transactions (e.g., $0.01 or $1.00) in rapid succession is a HIGH alert
Activities with merchant_id: MERCH_5555 and amount less than 10 is LOW alert, if amount greater than 100 that create HIGH alert
Activities for the same account from one more than 1 terminal_id greater than a 1-minute time difference is not an alert

```
----------------------


Start AI Processor Application


```shell
java -jar applications/alert-ai-processor/target/alert-ai-processor-0.0.1-SNAPSHOT.jar --stream.activity.filter.value=josiah --spring.profiles.active=finance --spring.ai.ollama.chat.options.model=llama3.2:latest --alerts.inference.batch=6
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

Start Josiah Alert App (critical alerts ONLY)


```shell
java -jar applications/alert-app/target/alert-app-0.0.1-SNAPSHOT.jar --spring.rabbitmq.host=localhost --spring.rabbitmq.username=guest --spring.rabbitmq.password=guest --spring.cloud.stream.bindings.input.destination="amq.topic" --stream.destination="alerts.alert" --stream.exchange.bind.key="#"   --stream.filter.sql="account = 'josiah' AND level IN ('critical')" --server.port=8778 --stream.activity.filter.name="account" --stream.activity.filter.value="josiah" --alert.refresh.rateSeconds=1
```

Open Web Application

```shell
open http://localhost:8778
```


10. Generator Activities

Start Generator to demo existing applications do not get generated
alerts and activities because of the RabbitMQ filtering.

```shell
java -jar applications/generator-supplier-source/target/generator-supplier-source-0.0.1-SNAPSHOT.jar --spring.cloud.stream.bindings.input.producer.poller.fixed-delay=1 --spring.cloud.stream.poller.fixedDelay=1 --spring.cloud.stream.poller.max-messages=1000000
```


Expected No new alerts or activities in running applications.
