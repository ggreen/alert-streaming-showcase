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

IOT-STREAM=iot-source --spring.application.name=iot-source --spring.profiles.active="amqp1.0" --source.amqp.filter.property.name="account" --server.port=9555 --spring.cloud.stream.bindings.output.destination="activities.activity" | alert-ai-processor --stream.activity.filter.value=josiah | alert-app --spring.cloud.stream.bindings.input.destination="amq.topic" --stream.destination="alerts.alert" --stream.exchange.bind.key="#"   --stream.filter.sql="account = 'josiah' AND level IN ('critical', 'high','medium','low')" --server.port=9777 --stream.activity.filter.name="account" --stream.activity.filter.value="josiah" --alert.refresh.rateSeconds=1
```

```shell
open http://localhost:9777
```


```shell
curl -X 'POST' \
  'http://localhost:9555/publisher?topic=josiah' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{ "id" : "70", "icon" : "fa-shield-alt", "account" : "josiah", "time" : "07:15 PM", "activity" : "Alarm System Turned OFF Successfully" }'

curl -X 'POST' \
  'http://localhost:9555/publisher?topic=josiah' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{ "id" : "71", "icon" : "fa-door-open",  "account" : "josiah", "time" : "07:14 PM", "activity" : "Front Door Opened" }'

curl -X 'POST' \
  'http://localhost:9555/publisher?topic=josiah' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{ "id" : "72", "icon" : "fa-temperature-low",  "account" : "josiah", "time" : "06:55 PM", "activity" : "Thermostat Set to 68°F (Cool)" }' 
   
curl -X 'POST' \
  'http://localhost:9555/publisher?topic=josiah' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{ "id" : "73", "icon" : "fa-door-open",  "account" : "josiah", "time" : "06:30 PM", "activity" : "Garage Door Opened" }'
  
curl -X 'POST' \
  'http://localhost:9555/publisher?topic=josiah' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{ "id" : "74", "icon" : "fa-shield-alt",  "account" : "josiah", "time" : "06:00 PM", "activity" : "Alarm System Turned ON (Away)" }'
  
curl -X 'POST' \
  'http://localhost:9555/publisher?topic=josiah' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{ "id" : "75", "icon" : "fa-temperature-high",  "account" : "josiah", "time" : "05:45 PM", "activity" : "Thermostat Set to 72°F (Heat)" }'

curl -X 'POST' \
  'http://localhost:9555/publisher?topic=josiah' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{ "id" : "76", "icon" : "fa-box",  "account" : "josiah", "time" : "05:00 PM", "activity" : "Refrigerator Door Ajar" }'
  
curl -X 'POST' \
  'http://localhost:9555/publisher?topic=josiah' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{ "id" : "77", "icon" : "fa-box", "account" : "josiah", "time" : "05:01 PM", "activity" : "Refrigerator Door Closed" }'
```
