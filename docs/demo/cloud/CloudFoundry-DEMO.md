# Payments Alert SQL Demo


5. Open RabbitMQ Management Dashboard

```shell
cf create-service-key rabbitmq rabbit-key -c '{"tags": "administrator"}'
cf service-key rabbitmq rabbit-key
```


```shell
export RABBIT_USERNAME=$(cf service-key rabbitmq rabbit-key | tail -n +3 | jq -r '.credentials.username')
export RABBIT_PASSWORD=$(cf service-key rabbitmq rabbit-key | tail -n +3 | jq -r '.credentials.password')
export RABBIT_VHOST=$(cf service-key rabbitmq rabbit-key | tail -n +3 | jq -r '.credentials.vhost')
export RABBIT_URL=$(cf service-key rabbitmq rabbit-key | tail -n +3 | jq -r '.credentials.dashboard_url')
echo "RabbitMQ User $RABBIT_USERNAME , Password:$RABBIT_PASSWORD vhost: $RABBIT_VHOST"
open $RABBIT_URL

```

----------------

Queues Queues

```shell
./deployment/cloud/cloudFoundry/scripts/delete-queues.sh
```
Expected No new alerts or activities in running applications.

```shell
cf restart alert-app
cf restart alert-critical
cf restart alert-josiah
cf restart alert-source
cf restart alert-ai-processor
cf restart activity-source
```

----------------

# Post Alerts

5. Open Activities & Alert Security Monitor App 


```shell
ALERT_APP_URL=`cf apps | awk '$1 == "alert-app" {print $NF}'`
echo opening $ALERT_APP_URL
open https://$ALERT_APP_URL
```

6. Post critical alerts


```shell
ALERT_ALL_URL=`cf apps | awk '$1 == "alert-app" {print $NF}'`
echo opening $ALERT_ALL_URL

open https://$ALERT_ALL_URL
```



9. Only Critical

```shell
ALERT_CRITICAL_URL=`cf apps | awk '$1 == "alert-critical" {print $NF}'`
echo opening $ALERT_CRITICAL_URL

open https://$ALERT_CRITICAL_URL
```

-----------------------------




11. Publish Activity

```shell
ACTIVITY_URL_SOURCE=`cf apps | awk '$1 == "activity-source" {print $NF}'`
echo opening $ACTIVITY_URL_SOURCE
open http://$ACTIVITY_URL_SOURCE
```


11. Restart Apps for Data as Service

Data as a service
```shell
cf stop alert-app
cf start alert-app
cf stop alert-critical
cf start alert-critical
```

Review Logs

------------------



# Artificial Intelligence


The following is an example prompt used for fraud
detection.

```text
Given the following payment activities, identify potential fraud alerts, 
For each alert response with the "level" with values of (CRITICAL, HIGH, MEDIUM, Low),
the time and the event which contains why you believe this is an alert
ONLY RESPONSE the Json Object fields level, time, and event

[ACTIVITIES]
```

Example

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

Open AI Agent Processor


```shell
ALERT_AI_PROCESSOR_URL=`cf apps | awk '$1 == "alert-ai-processor" {print $NF}'`
echo opening $ALERT_AI_PROCESSOR_URL
open https://$ALERT_AI_PROCESSOR_URL
```

Click Similar Fraud in Activity Source

```shell
ACTIVITY_URL_SOURCE=`cf apps | awk '$1 == "activity-source" {print $NF}'`
echo opening $ACTIVITY_URL_SOURCE
open http://$ACTIVITY_URL_SOURCE
```


Start Josiah Alert App


```shell
ALERT_JOSIAH_URL_SOURCE=`cf apps | awk '$1 == "alert-josiah" {print $NF}'`
echo opening $ALERT_JOSIAH_URL_SOURCE 
open https://$ALERT_JOSIAH_URL_SOURCE

```


10. Generator Activities

Start Generator to demo existing applications do not get generated
alerts and activities because of the RabbitMQ filtering.

Reviews stats in RabbitMQ console

```shell
cf start generator-supplier-source
```


DO NOT FORGET TO STOP Generator

```shell
cf stop generator-supplier-source
```
