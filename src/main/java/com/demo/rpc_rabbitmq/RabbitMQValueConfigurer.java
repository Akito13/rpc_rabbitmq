package com.demo.rpc_rabbitmq;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQValueConfigurer {

    public static String RABBITMQ_HOST;
    @Value("${spring.rabbitmq.host}")
    public void setRabbitmqHost(String host) {
        RabbitMQValueConfigurer.RABBITMQ_HOST = host;
    }
//    public static String getRABBITMQ_HOST() {
//        return RABBITMQ_HOST;
//    }

    public static int RABBITMQ_PORT;
    @Value("${spring.rabbitmq.port}")
    public void setRabbitmqPort(int port) {
        RabbitMQValueConfigurer.RABBITMQ_PORT = port;
    }

    public static String RABBITMQ_USERNAME;
    @Value("${spring.rabbitmq.username}")
    public void setRabbitmqUsername(String username) {
        RabbitMQValueConfigurer.RABBITMQ_USERNAME = username;
    }

    public static String RABBITMQ_PASSWORD;
    @Value("${spring.rabbitmq.password}")
    public void setRabbitmqPassword(String password) {
        RabbitMQValueConfigurer.RABBITMQ_PASSWORD = password;
    }

    public static final String RABBITMQ_CORRELATION_ID = "correlationId";

    public static final String RABBITMQ_EXCHANGE_NAME = "ex.rpc-requests";

    public static String RABBITMQ_RPC_REQUESTS_QUEUE = "rpc-requests";
    public static String RABBITMQ_RPC_RESPONSES_QUEUE = "rpc-responses";
    public static String RABBITMQ_EXCHANGE_TYPE = "topic";
    public static String RABBITMQ_RPC_REQUEST_ROUTING_KEY = "rpc.request.key";
    public static String RABBITMQ_RPC_RESPONSE_ROUTING_KEY = "rpc.response.key";


}
