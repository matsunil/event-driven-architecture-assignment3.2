package com.thoughtmechanix.organization;

import com.thoughtmechanix.organization.utils.UserContextFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import javax.servlet.Filter;

/**
 * The @EnableBinding annotation tells Spring Cloud Stream that you 
 * want to bind the service to a message broker
 * The use of Source.class in the @EnableBinding annotation tells Spring Cloud 
 * Stream that this service will communicate with the message broker via a set of channels 
 * defined on the Source class.
 * Remember, channels sit above a message queue. Spring Cloud Stream has a default 
 * set of channels that can be configured to speak to a message broker.
 * At this point you haven’t told Spring Cloud Stream what message broker you want 
 * the organization service to bind to. 
 * We’ll get to that in events/source/SimpleSourceBean.java class.
 */
@SpringBootApplication
@EnableEurekaClient
@EnableCircuitBreaker
@EnableBinding(Source.class)
public class Application {
    @Bean
    public Filter userContextFilter() {
        UserContextFilter userContextFilter = new UserContextFilter();
        return userContextFilter;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
