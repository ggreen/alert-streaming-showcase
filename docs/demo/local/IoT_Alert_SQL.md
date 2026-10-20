# IoT Alert SQL Demo

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
java -jar applications/http-source/target/http-source-0.0.1.jar \
  --mqtt.connectionUrl=tcp://localhost:1883 \
  --spring.application.name=http-mqtt-source \
  --mqtt.userName=guest \
  --mqtt.userPassword=guest --spring.profiles.active=mqtt
```

3. Start Alarm app for all imani alerts

```shell
java -jar applications/alert-app/target/alert-app-0.0.1.jar --spring.rabbitmq.host=localhost --spring.rabbitmq.username=guest --spring.rabbitmq.password=guest --spring.cloud.stream.bindings.input.destination="amq.topic" --stream.destination="alerts.alert" --stream.exchange.bind.key="#"   --stream.filter.sql="account = 'imani' AND level IN ('critical', 'high','medium','low')" --server.port=8080 --stream.activity.filter.name="account" --stream.activity.filter.value="imani" --alert.refresh.rateSeconds=1
```


4. Start app CRITICAL ONLY alerts

```shell
java -jar applications/alert-app/target/alert-app-0.0.1.jar --spring.rabbitmq.host=localhost --spring.application.name="imani-critical" --spring.rabbitmq.username=guest --spring.rabbitmq.password=guest --spring.cloud.stream.bindings.input.destination="amq.topic" --stream.destination="alerts.alert" --stream.exchange.bind.key="#"   --stream.filter.sql="account = 'imani' AND level IN ('critical')" --server.port=8911 --stream.activity.filter.name="account" --stream.activity.filter.value="imani" --alert.refresh.rateSeconds=1
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
  -d '{"id" : "01", "account" : "imani", "level" : "critical", "time" : "5:00AM", "event" : "Break-in in progress" }'
```

7. Post Alert

```shell
curl -X 'POST' \
  'http://localhost:8383/publisher?topic=alerts' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -H "account: imani" \
  -H "level: high" \
  -d '{"id" : "02", "account" : "imani", "level" : "high", "time" : "6:15AM", "event" : "Time to wakeup!" }'
  
```


7. Post Alert

```shell
curl -X 'POST' \
  'http://localhost:8383/publisher?topic=alerts' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -H "account: imani" \
  -H "level: medium" \
  -d '{"id" : "03", "account" : "imani", "level" : "medium", "time" : "7:00AM", "event" : "BUS COMING!!" }'
  
```


6. Post Alert

```shell
curl -X 'POST' \
  'http://localhost:8383/publisher?topic=alerts' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -H "account: imani" \
  -H "level: low" \
  -d '{"id" : "04", "account" : "imani", "level" : "low", "time" : "7:00AM", "event" : "BUS Left!!" }'
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
java -jar applications/http-source/target/http-source-0.0.1.jar \
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
  "icon" : "fa-door-open",
  "time" : "06:30 PM", 
  "activity" : "Garage Door Opened"
}'
curl -X 'POST' \
  'http://localhost:8555/publisher?topic=imani' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "id" :  "2", 
  "account" : "imani",
  "icon" : "fa-door-closed",
  "time" : "06:31 PM", 
  "activity" : "Garage Door Closed"
}'

curl -X 'POST' \
  'http://localhost:8555/publisher?topic=imani' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "id" :  "3", 
  "account" : "imani",
  "icon" : "fa-door-open",
  "time" : "06:33 PM", 
  "activity" : "Garage Door Opened"
}'
curl -X 'POST' \
  'http://localhost:8555/publisher?topic=imani' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "id" :  "4", 
  "account" : "imani",
  "icon" : "fa-door-closed",
  "time" : "06:31 PM", 
  "activity" : "Garage Door Closed"
}'
```


11. Restart Apps for Data as Service

Data as a service

------------------

# Artificial Intelligence

Given the following activities, identify alerts, 
For each alert response with the "level" with values of (CRITICAL, HIGH, MEDIUM, Low),
the time and the event which contains why you believe this is an alert

ONLY RESPONSE the Json Object fields level, time, and event

```json
{ "icon" : "fa-shield-alt",  "account" : "josiah", "time" : "07:15 PM", "activity" : "Alarm System Turned OFF" },
{ "icon" : "fa-door-open",   "account" : "josiah", "time" : "07:14 PM", "activity" : "Front Door Opened" },
{ "icon" : "fa-door-closed"",   "account" : "josiah", "time" : "07:14 PM"", "activity" : "Front Door Closed" },
{ "icon" : "fa-temperature-low",   "account" : "josiah", "time" : "06:55 PM", "activity" : "Thermostat Set to 68°F (Cool)" },
{ "icon" : "fa-door-open",   "account" : "josiah", "time" : "06:30 PM", "activity" : "Garage Door Opened" },
{ "icon" : "fa-door-closed",   "account" : "josiah", "time" : "06:31 PM", "activity" : "Garage Door Closed" },
{ "icon" : "fa-shield-alt",   "account" : "josiah", "time" : "06:00 PM", "activity" : "Alarm System Turned ON (Away)" },
{ "icon" : "fa-temperature-high",   "account" : "josiah", "time" : "05:45 PM", "activity" : "Thermostat Set to 72°F (Heat)" },
{ "icon" : "fa-box",   "account" : "josiah", "time" : "05:00 PM", "activity" : "Refrigerator Door Ajar" },
{ "icon" : "fa-box",   "account" : "josiah", "time" : "05:01 PM", "activity" : "Refrigerator Door Closed" }
```

Use Context Below
CONTEXT: 
DO NOT alert if Opened door or Garage Door AND the door is closed at a later time
DO NOT alert if Alarm System Turned On

-------------------------------------


Given the following activities, identify alerts,
For each alert response with the "level" with values of (critical, high, medium, low),
the time and the event which contains why you believe this is an alert

ONLY RESPONSE the Json Object fields level, time, and event

```json
[{ "icon" : "fa-shield-alt",   "account" : "josiah", "time" : "07:15 PM", "activity" : "Alarm System Turned OFF Successfully" }, 
{ "icon" : "fa-door-open",   "account" : "josiah", "time" : "07:14 PM", "activity" : "Front Door Opened" },
{ "icon" : "fa-temperature-low",   "account" : "josiah", "time" : "06:55 PM", "activity" : "Thermostat Set to 68°F (Cool)" },
{ "icon" : "fa-door-open",   "account" : "josiah", "time" : "06:30 PM", "activity" : "Garage Door Opened" },
{ "icon" : "fa-shield-alt",   "account" : "josiah", "time" : "06:00 PM", "activity" : "Alarm System Turned ON (Away)" },
{ "icon" : "fa-temperature-high",   "account" : "josiah", "time" : "05:45 PM", "activity" : "Thermostat Set to 72°F (Heat)" },
{ "icon" : "fa-box",   "account" : "josiah", "time" : "05:00 PM", "activity" : "Refrigerator Door Ajar" },
{ "icon" : "fa-box",   "account" : "josiah", "time" : "05:01 PM", "activity" : "Refrigerator Door Closed" }]
```

Use Context Below
CONTEXT:
DO NOT alert when Opened door or Garage Door AND the door is closed at a later time
DO NOT alert when Alarm System Turned On
DO NOT alert when Alarm System Turned OFF Successfully
Door Opened with No Door Closed is a MEDIUM Alert


----------------------


Start AI Processor Application


```shell
java -jar applications/alert-ai-processor/target/alert-ai-processor-0.0.1.jar --stream.activity.filter.value=josiah --spring.profiles.active=iot
```

```shell
curl -X 'POST' \
  'http://localhost:8555/publisher?topic=josiah' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{ "id" : "70", "icon" : "fa-shield-alt", "account" : "josiah", "time" : "07:15 PM", "activity" : "Alarm System Turned OFF Successfully" }'

curl -X 'POST' \
  'http://localhost:8555/publisher?topic=josiah' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{ "id" : "71", "icon" : "fa-door-open",  "account" : "josiah", "time" : "07:14 PM", "activity" : "Front Door Opened" }'

curl -X 'POST' \
  'http://localhost:8555/publisher?topic=josiah' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{ "id" : "72", "icon" : "fa-temperature-low",  "account" : "josiah", "time" : "06:55 PM", "activity" : "Thermostat Set to 68°F (Cool)" }' 
   
curl -X 'POST' \
  'http://localhost:8555/publisher?topic=josiah' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{ "id" : "73", "icon" : "fa-door-open",  "account" : "josiah", "time" : "06:30 PM", "activity" : "Garage Door Opened" }'
  
curl -X 'POST' \
  'http://localhost:8555/publisher?topic=josiah' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{ "id" : "74", "icon" : "fa-shield-alt",  "account" : "josiah", "time" : "06:00 PM", "activity" : "Alarm System Turned ON (Away)" }'
  
curl -X 'POST' \
  'http://localhost:8555/publisher?topic=josiah' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{ "id" : "75", "icon" : "fa-temperature-high",  "account" : "josiah", "time" : "05:45 PM", "activity" : "Thermostat Set to 72°F (Heat)" }'

curl -X 'POST' \
  'http://localhost:8555/publisher?topic=josiah' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{ "id" : "76", "icon" : "fa-box",  "account" : "josiah", "time" : "05:00 PM", "activity" : "Refrigerator Door Ajar" }'
  
curl -X 'POST' \
  'http://localhost:8555/publisher?topic=josiah' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{ "id" : "77", "icon" : "fa-box", "account" : "josiah", "time" : "05:01 PM", "activity" : "Refrigerator Door Closed" }'
curl -X 'POST' \
  'http://localhost:8555/publisher?topic=josiah' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{ "id" : "78", "icon" : "fa-box", "account" : "josiah", "time" : "05:03 PM", "activity" : "Alarm System Turned ON Successfully" }'
  
curl -X 'POST' \
  'http://localhost:8555/publisher?topic=josiah' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{ "id" : "79", "icon" : "fa-box", "account" : "josiah", "time" : "08:03 PM", "activity" : "Alarm System TRIGGER possible BREAK-IN in progress, CALL 911!" }'
  
curl -X 'POST' \
  'http://localhost:8555/publisher?topic=josiah' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{ "id" : "80", "icon" : "fa-box", "account" : "josiah", "time" : "08:03 PM", "activity" : "Window Broken ALARM TRIGGERED possible BREAK-IN in progress, CALL 911!" }'
  
curl -X 'POST' \
  'http://localhost:8555/publisher?topic=josiah' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{ "id" : "81", "icon" : "fa-door-open", "account" : "josiah", "time" : "09:03 PM", "activity" : "Front Door Broken ALARM TRIGGERED possible BREAK-IN in progress, CALL 911!" }'
  
curl -X 'POST' \
  'http://localhost:8555/publisher?topic=josiah' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{ "id" : "82", "icon" : "fas fa-camera", "account" : "josiah", "time" : "09:04 PM", "activity" : "Camera Broken ALARM TRIGGERED possible BREAK-IN in progress, CALL 911!" }'
```


Start Josiah Alert App


```shell
java -jar applications/alert-app/target/alert-app-0.0.1.jar --spring.rabbitmq.host=localhost --spring.rabbitmq.username=guest --spring.rabbitmq.password=guest --spring.cloud.stream.bindings.input.destination="amq.topic" --stream.destination="alerts.alert" --stream.exchange.bind.key="#"   --stream.filter.sql="account = 'josiah' AND level IN ('critical', 'high','medium','low')" --server.port=8777 --stream.activity.filter.name="account" --stream.activity.filter.value="josiah" --alert.refresh.rateSeconds=1
```

```shell
open http://localhost:8777
```

10. Generator Activities

```shell
java -jar applications/generator-supplier-source/target/generator-supplier-source-0.0.1.jar --spring.cloud.stream.bindings.input.producer.poller.fixed-delay=1 --spring.cloud.stream.poller.fixedDelay=1 --spring.cloud.stream.poller.max-messages=1000000
```


No new alerts

-----------------------

# SCDF 

Register Applications

```shell
echo source.iot-source=file://$PWD/applications/http-source/target/http-source-0.0.1.jar
echo source.iot-source.bootVersion=3
echo processor.alert-ai-processor=file://$PWD/applications/alert-ai-processor/target/alert-ai-processor-0.0.1.jar
echo processor.alert-ai-processor.bootVersion=3
echo sink.alert-app=file://$PWD/applications/alert-app/target/alert-app-0.0.1.jar
echo sink.alert-app.bootVersion=3
```


```shell
open http://localhost:9393/dashboard/index.html#/apps
```

```shell

IOT-STREAM_CRITICAL=iot-source --spring.application.name=iot-source --spring.profiles.active="amqp1.0" --source.amqp.filter.property.name="account" --server.port=9555 --spring.cloud.stream.bindings.output.destination="activities.activity" | alert-app --spring.cloud.stream.bindings.input.destination="amq.topic" --stream.destination="alerts.alert" --stream.exchange.bind.key="#"   --stream.filter.sql="account = 'josiah' AND level IN ('critical', 'high')" --server.port=9777 --stream.activity.filter.name="account" --stream.activity.filter.value="josiah" --alert.refresh.rateSeconds=1
```


```shell
open http://localhost:9777
```