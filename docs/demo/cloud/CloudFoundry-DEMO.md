# Tanzu Platform RabbitMQ Event Streaming Demo


![img.png](img.png)

## 1. Open RabbitMQ Management Dashboard

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

## 2. Create Queues

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

## 3. Open Activities & Alert Security Monitor App 


```shell
ALERT_APP_URL=`cf apps | awk '$1 == "alert-app" {print $NF}'`
echo opening $ALERT_APP_URL
open https://$ALERT_APP_URL
```

## 4. Post critical alerts


```shell
ALERT_ALL_URL=`cf apps | awk '$1 == "alert-app" {print $NF}'`
echo opening $ALERT_ALL_URL

open https://$ALERT_ALL_URL
```



## 5. Open Only Critical Alerts

```shell
ALERT_CRITICAL_URL=`cf apps | awk '$1 == "alert-critical" {print $NF}'`
echo opening $ALERT_CRITICAL_URL

open https://$ALERT_CRITICAL_URL
```


## 6.  Publish Activity

```shell
ACTIVITY_URL_SOURCE=`cf apps | awk '$1 == "activity-source" {print $NF}'`
echo opening $ACTIVITY_URL_SOURCE
open http://$ACTIVITY_URL_SOURCE
```


## 7.  Restart Apps for Data as Service

Data as a service
```shell
cf stop alert-app
cf start alert-app
cf stop alert-critical
cf start alert-critical
```

Review Logs

------------------



## 8. Open AI Agent Processor

```shell
ALERT_AI_PROCESSOR_URL=`cf apps | awk '$1 == "alert-ai-processor" {print $NF}'`
echo opening $ALERT_AI_PROCESSOR_URL
open https://$ALERT_AI_PROCESSOR_URL
```

Click Simulate Fraud in Activity Source

```shell
ACTIVITY_URL_SOURCE=`cf apps | awk '$1 == "activity-source" {print $NF}'`
echo opening $ACTIVITY_URL_SOURCE
open http://$ACTIVITY_URL_SOURCE
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

Example

```text
Use Context Below
CONTEXT:

Activities for the same account from one more than 1 terminal_id within 1 minute time differences is a CRITICAL alert.
Activities for the same account from one more than 1 terminal_id within 5 minute time differences is a HIGH alert.
Activities with a series of very small transactions (e.g., $0.01 or $1.00) in rapid succession is a HIGH alert
Activities with merchant_id: MERCH_5555 and amount less than 10 is LOW alert, if amount greater than 100 that create HIGH alert
Activities for the same account from one more than 1 terminal_id greater than a 1-minute time difference is not an alert

```



## 9. Start Josiah Alert App


```shell
ALERT_JOSIAH_URL_SOURCE=`cf apps | awk '$1 == "alert-josiah" {print $NF}'`
echo opening $ALERT_JOSIAH_URL_SOURCE 
open https://$ALERT_JOSIAH_URL_SOURCE

```


## 10.  Generator Activities

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
