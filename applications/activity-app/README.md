
Start Application





```shell
java -jar applications/alert-app/target/alert-app-0.0.1-SNAPSHOT.jar --stream.filter.sql="account = 'imani' AND level IN ('critical', 'high','medium','low')"
```



```json
{
  "id": "01",
  "level": "Critical",
  "time": "02:22 PM",
  "dateTime": "2025-11-30T23:54:17.285Z",
  "event": "Testing"
}
```

```shell
curl -X 'POST' \
  'http://localhost:8080/alert' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "id": "01",
  "level": "Critical",
  "time": "02:22 PM",
  "event": "Testing"
}'
```


```shell
curl -X 'POST' \
  'http://localhost:8080/activities/activity' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{  "id": "03", "icon": "fa-shield-alt", "time": "07:15 PM", "activity": "Alarm System Turned OFF" }'
  
```

```shell
curl -X 'POST' \
  'http://localhost:8080/activities/activity' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{ "id": "02", "icon": "fa-door-open", "time": "07:14 PM", "activity": "Front Door Opened" }'
  
```
