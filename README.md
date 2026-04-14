# Alert Showcase


Real Time  alarm-event-streaming-showcase using [Spring AI](https://spring.io/projects/spring-ai) and [RabbitMQ Streams SQL filtering](https://www.rabbitmq.com/blog/2025/09/23/sql-filter-expressions).

![RealTime AI-ML RabbitMQ and Spring AI](docs/img/architecture.png)

This demo uses [RabbitMQ Streams SQL filtering](https://www.rabbitmq.com/blog/2025/09/23/sql-filter-expressions). 
It demonstrates the power of RabbitMQ to deliver high throughput 
messaging with flexible, routing and filtering.

It also demonstrates data ingestion from sourcing such as Internet of things devices 
for various use cases, such as home security and financial real time payment fraud detection.


The applications are implemented using [Spring](https://spring.io/) [Boot](https://spring.io/projects/spring-boot). 
[Spring AI](https://spring.io/projects/spring-ai) is used significantly for integrating real time AI/ML with RabbitMQ SQL filtering.

# Demo Instructions


## IoT Home Security 

![ui.png](docs/img/ui.png)
See the instructions
- [Iot Home Security Demo Instructions](docs/demo/local/IoT_Alert_SQL.md)


## financial real time payment fraud detection.

![financial_demo_img.png](docs/img/financial_demo_img.png)

See instructions
- [Financial Payment RealTime Fraud Detection Demo Instructions](docs/demo/local/Payments_Alert_SQL.md)

## Cleanup


```shell
rabbitmqadmin delete exchange --name=activities.activity
rabbitmqadmin delete exchange --name=alerts.alert
rabbitmqadmin delete exchange --name=alert
rabbitmqadmin delete queue --name=perfTest
rabbitmqadmin delete queue --name=activities.activity
rabbitmqadmin delete queue --name=activities.activity.dlq
rabbitmqadmin delete queue --name=activities.super.stream-2
rabbitmqadmin delete exchange --name=activities.super.stream
rabbitmqadmin delete exchange --name=activityConsumer-in-0
rabbitmqadmin delete queue --name=alerts.alert
rabbitmqadmin delete queue --name=activities.super.stream
rabbitmqadmin delete queue --name=activities.super.stream-0
rabbitmqadmin delete queue --name=activities.super.stream-1
```
