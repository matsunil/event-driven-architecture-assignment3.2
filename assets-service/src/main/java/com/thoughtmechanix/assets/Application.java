package com.thoughtmechanix.assets;

import com.thoughtmechanix.assets.config.ServiceConfig;
import com.thoughtmechanix.assets.events.models.OrganizationChangeModel;
import com.thoughtmechanix.assets.utils.UserContextInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.core.MessageSource;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.client.RestTemplate;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Because the assets service is a consumer of a message, you’re going to pass the
 * @EnableBinding annotation the value Sink.class. This tells Spring Cloud Stream
 * to bind to a message broker using the default Spring Sink interface
 *
 */
@SpringBootApplication
@EnableEurekaClient
@EnableCircuitBreaker 
@EnableBinding(Sink.class)
public class Application {

    @Autowired
    private ServiceConfig serviceConfig;

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    @LoadBalanced
    @Bean
    public RestTemplate getRestTemplate() {
        RestTemplate template = new RestTemplate();
        List interceptors = template.getInterceptors();
        if (interceptors == null) {
            template.setInterceptors(Collections.singletonList(new UserContextInterceptor()));
        } else {
            interceptors.add(new UserContextInterceptor());
            template.setInterceptors(interceptors);
        }

        return template;
    }

    //To communicate with a specific Redis instance, expose a JedisConnectionFactory as a Spring Bean
    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory jedisConnFactory = new JedisConnectionFactory();
        jedisConnFactory.setHostName( serviceConfig.getRedisServer());
        jedisConnFactory.setPort( serviceConfig.getRedisPort() );
        return jedisConnFactory;
    }

    /* Once you have a connection out to Redis, you’re going to use that connection to create 
    	   a Spring RedisTemplate object. The RedisTemplate object will be used by the Spring Data 
    	   repository classes that you’ll implement shortly to execute the queries and saves of organization service
    	   data to your Redis service.
    */
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(jedisConnectionFactory());
        return template;
    }

//    @StreamListener(Sink.INPUT)
//    public void loggerSink(OrganizationChangeModel orgChange) {
//        logger.debug("Received an event for organization id {}", orgChange.getOrganizationId());
//    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
