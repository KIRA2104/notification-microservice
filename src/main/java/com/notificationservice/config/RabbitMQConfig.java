package com.notificationservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "notifications.exchange";
    public static final String EMAIL_QUEUE = "notifications.email";
    public static final String SMS_QUEUE = "notifications.sms";
    public static final String PUSH_QUEUE = "notifications.push";

    @Bean
    public DirectExchange notificationExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue emailQueue() {
        return new Queue(EMAIL_QUEUE, true);
    }

    @Bean
    public Queue smsQueue() {
        return new Queue(SMS_QUEUE, true);
    }

    @Bean
    public Queue pushQueue() {
        return new Queue(PUSH_QUEUE, true);
    }

    @Bean
    public Binding emailBinding(Queue emailQueue, DirectExchange notificationExchange) {
        return BindingBuilder.bind(emailQueue).to(notificationExchange).with("EMAIL");
    }

    @Bean
    public Binding smsBinding(Queue smsQueue, DirectExchange notificationExchange) {
        return BindingBuilder.bind(smsQueue).to(notificationExchange).with("SMS");
    }

    @Bean
    public Binding pushBinding(Queue pushQueue, DirectExchange notificationExchange) {
        return BindingBuilder.bind(pushQueue).to(notificationExchange).with("PUSH");
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
