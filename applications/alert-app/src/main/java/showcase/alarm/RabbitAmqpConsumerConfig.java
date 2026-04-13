package showcase.alarm;

import com.rabbitmq.client.amqp.*;
import com.rabbitmq.client.amqp.impl.AmqpEnvironmentBuilder;
import lombok.extern.slf4j.Slf4j;
import nyla.solutions.core.patterns.conversion.Converter;
import nyla.solutions.core.util.Debugger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import showcase.streaming.domains.Activity;
import showcase.streaming.domains.Alert;

@Configuration
@Slf4j
class RabbitAmqpConsumerConfig {

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${spring.rabbitmq.host:localhost}")
    private String host;

    @Value("${spring.rabbitmq.username:guest}")
    private String username;

    @Value("${spring.rabbitmq.password:guest}")
    private String password;


    @Value("${stream.filter.offset:FIRST}")
    private String offsetName;

    //@Value("${spring.cloud.stream.bindings.input.destination:inputFilter}.${spring.cloud.stream.bindings.input.group}")
    @Value("${stream.destination.alerts:alerts.alert}")
    private String alertStream;

    @Value("${stream.filter.sql:}")
    private String sqlFilter;

    @Value("${stream.alert.filter.value:}")
    private String alertFilterValue;

    @Value("${stream.activity.filter.value:}")
    private String activityFilterValue;

    @Value("${stream.activity.filter.name:}")
    private String activityFilterPropertyName;


    @Value("${spring.cloud.stream.bindings.input.destination:amq.topic}")
    private String alertExchange;


    @Value("${stream.alert.exchange.bind.key:#}")
    private String alertBindRoutingKey;

    @Value("${stream.activity.stream:activities.activity}")
    private String activityStream;

    @Value("${stream.activity.exchange:#}")
    private String activityExchange;


    @Value("${stream.activity.exchange.bind.key:#}")
    private String activityBindRoutingKey;


    @Bean
    Environment amqpEnvironment()
    {
        return new AmqpEnvironmentBuilder()
                .connectionSettings()
                .host(host)
                .username(username)
                .password(password)
                .environmentBuilder()
                .build();
    }

    @Bean("alertConnection")
    Connection alertConnection(Environment environment)
    {
        return environment.connectionBuilder().host(host)
                .name(applicationName)
                .username(username)
                .password(password)
                .build();
    }

    @Bean("activityConnection")
    Connection activityConnection(Environment environment)
    {
        return environment.connectionBuilder().host(host)
                .name(applicationName)
                .username(username)
                .password(password)
                .build();
    }



    @Bean
    Consumer alertRabbitConsumer(@Qualifier("alertConnection") Connection connection,
                           @Qualifier("alertQueue") Management.QueueInfo input,
                           java.util.function.Consumer<Alert> alertConsumer,
                           Converter<byte[], Alert> messageConverter){

        log.info("input consumed with SQL '{}' from input stream {}",sqlFilter,input.name());

        var builder = connection.consumerBuilder()
                .queue(input.name())
                .stream()
                .offset(ConsumerBuilder.StreamOffsetSpecification.valueOf(offsetName));

        if(!alertFilterValue.isEmpty())
        {
            log.info("Adding filter value: {}", alertFilterValue);
            builder = builder.filterValues(alertFilterValue);
        }

        Consumer.MessageHandler handler = (ctx,inputMessage) -> {

            try {
                //Processing input message
                log.info("Processing input: {}, msg id: {}", alertStream, inputMessage.messageId());
                alertConsumer.accept(messageConverter.convert(inputMessage.body()));
                ctx.accept();
            }
            catch (Exception e)
            {
                log.error(Debugger.stackTrace(e));
                throw e;
            }

        };

        if(sqlFilter != null && !sqlFilter.isEmpty())
        {
            //Use SQL Filter
            return builder
                    .filter()
                    .sql(sqlFilter)
                    .stream()
                    .builder().messageHandler(handler)
                    .build();
        }
        else {
            //No SQL Filter
            return builder.builder()
                    .messageHandler(handler)
                    .build();
        }

    }

    @Bean
    Consumer activityRabbitConsumer(@Qualifier("activityConnection") Connection connection,
                           @Qualifier("activityQueue") Management.QueueInfo input,
                           java.util.function.Consumer<Activity> activityConsumer,
                           Converter<byte[], Activity> messageConverter){

        log.info("Activities  from input stream {}", input.name());

        var builder = connection.consumerBuilder()
                .queue(input.name())
                .stream()
                .offset(ConsumerBuilder.StreamOffsetSpecification.valueOf(offsetName));

        if(!activityFilterPropertyName.isEmpty() && !activityFilterValue.isEmpty())
        {
            log.info("Adding activity filter value: {}", activityFilterValue);
            builder = builder
                    .filter()
                    .property(activityFilterPropertyName,activityFilterValue)
                    .stream();

        }

        return builder
                .builder().messageHandler((ctx,inputMessage) -> {
                    try {
                        //Processing input message
                        log.info("Processing input: {}, msg id: {}", activityStream, inputMessage.messageId());
                        activityConsumer.accept(messageConverter.convert(inputMessage.body()));
                        ctx.accept();
                    }
                    catch (Exception e)
                    {
                        log.error(Debugger.stackTrace(e));
                        throw e;
                    }

                })
                .build();
    }


    @Bean("alertManagement")
    Management alertManagement(@Qualifier("alertConnection") Connection connection)
    {
        return connection.management();
    }

    @Bean("alertQueue")
    Management.QueueInfo alertQueue(@Qualifier("alertManagement") Management management)
    {

        var queue = management
                .queue()
                .name(alertStream)
                .stream()
                .queue()
                .declare();

        management.exchange(alertExchange)
                        .type(Management.ExchangeType.TOPIC)
                                .declare();

        management.binding().sourceExchange(alertExchange)
                .destinationQueue(alertStream)
                .key(alertBindRoutingKey)
                .bind();

         return queue;

    }

    @Bean("activityManagement")
    Management amqpManagement(@Qualifier("activityConnection") Connection connection)
    {
        return connection.management();
    }

    @Bean("activityQueue")
    Management.QueueInfo inputQueue(@Qualifier("activityManagement") Management management)
    {
        var queue = management
                .queue()
                .name(activityStream)
                .stream()
                .queue()
                .declare();

        management.exchange(activityExchange)
                .type(Management.ExchangeType.TOPIC)
                .declare();

        management.binding().sourceExchange(activityExchange)
                .destinationQueue(activityStream)
                .key(activityBindRoutingKey)
                .bind();

        return queue;
    }

}
