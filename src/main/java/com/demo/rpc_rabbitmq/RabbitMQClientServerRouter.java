package com.demo.rpc_rabbitmq;

import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.DelayDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RabbitMQClientServerRouter extends RouteBuilder {

    @Autowired
    private RedisService redisService;

    @Override
    public void configure() throws Exception {
        System.out.println(RabbitMQValueConfigurer.RABBITMQ_HOST);
//      Client sending request
        from("timer://rpc-client?period=10000&fixedRate=true")
            .routeId("rpc-client-route")
            .process(exchange -> {
                String correlationId = UUID.randomUUID().toString();
                exchange.getMessage().setHeader(RabbitMQValueConfigurer.RABBITMQ_CORRELATION_ID, correlationId);
                redisService.save(correlationId);
                exchange.getMessage().setBody("Give random number");
            })
            .log("Request sent: ${body} with correlationId: ${header.correlationId}")
            .toF("spring-rabbitmq:%s?queues=%s&routingKey=%s&exchangeType=%s",
                    RabbitMQValueConfigurer.RABBITMQ_EXCHANGE_NAME,
                    RabbitMQValueConfigurer.RABBITMQ_RPC_REQUESTS_QUEUE,
                    RabbitMQValueConfigurer.RABBITMQ_RPC_REQUEST_ROUTING_KEY,
                    RabbitMQValueConfigurer.RABBITMQ_EXCHANGE_TYPE)
            .end();

//      Client receiving response
        fromF("spring-rabbitmq:%s?queues=%s&routingKey=%s&exchangeType=%s",
                RabbitMQValueConfigurer.RABBITMQ_EXCHANGE_NAME,
                RabbitMQValueConfigurer.RABBITMQ_RPC_RESPONSES_QUEUE,
                RabbitMQValueConfigurer.RABBITMQ_RPC_RESPONSE_ROUTING_KEY,
                RabbitMQValueConfigurer.RABBITMQ_EXCHANGE_TYPE)
            .routeId("rpc-client-response-route")
            .filter(exchange -> {
                String correlationId = exchange.getMessage().getHeader(RabbitMQValueConfigurer.RABBITMQ_CORRELATION_ID, String.class);
                boolean isValid = redisService.isValidCorrelationId(correlationId);
                if (!isValid) {
                    System.out.println("Invalid correlationId: " + correlationId);
                }
                return isValid;
            })
            .log("Response received: ${body} with correlationId: ${header.correlationId}")
            .end();

//      Server receiving request
        fromF("spring-rabbitmq:%s?queues=%s&routingKey=%s&exchangeType=%s",
                RabbitMQValueConfigurer.RABBITMQ_EXCHANGE_NAME,
                RabbitMQValueConfigurer.RABBITMQ_RPC_REQUESTS_QUEUE,
                RabbitMQValueConfigurer.RABBITMQ_RPC_REQUEST_ROUTING_KEY,
                RabbitMQValueConfigurer.RABBITMQ_EXCHANGE_TYPE)
            .routeId("rpc-server-route")
            .log("Request received: ${body} with correlationId: ${header.correlationId}")
            .setBody(exchange -> "Random number: " + Math.random())
            .delay(5000)
            .toF("spring-rabbitmq:%s?queues=%s&routingKey=%s&exchangeType=%s",
                    RabbitMQValueConfigurer.RABBITMQ_EXCHANGE_NAME,
                    RabbitMQValueConfigurer.RABBITMQ_RPC_RESPONSES_QUEUE,
                    RabbitMQValueConfigurer.RABBITMQ_RPC_RESPONSE_ROUTING_KEY,
                    RabbitMQValueConfigurer.RABBITMQ_EXCHANGE_TYPE)
            .log("Response sent: ${body} with correlationId: ${header.correlationId}")
            .end();
    }
}
