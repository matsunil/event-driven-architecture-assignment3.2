package com.thoughtmechanix.organization.events.source;

import com.thoughtmechanix.organization.events.models.OrganizationChangeModel;
import com.thoughtmechanix.organization.utils.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * We inject the Spring Cloud Source class into this class. All communication 
 * to a specific message topic occurs through a Spring Cloud Stream construct called a channel. 
 * A channel is represented here by a Java interface Source.
 * The Source interface is a Spring Cloud defined interface that exposes a single method 
 * called output(). The Source interface is a convenient interface to use when your service 
 * only needs to publish to a single channel. The output() method returns a class of type MessageChannel.
 *
 */
@Component
public class SimpleSourceBean {
    private Source source;

    private static final Logger logger = LoggerFactory.getLogger(SimpleSourceBean.class);

    @Autowired
    public SimpleSourceBean(Source source){
        this.source = source;
    }

    //The actual publication of the message occurs in this method.
    public void publishOrgChange(String action,String orgId) {
       logger.debug("Sending Kafka message {} for Organization Id: {}", action, orgId);
       
        //Create instance of events/model/OrganizationChangeMode for passed action and orgId
        OrganizationChangeModel change =  new OrganizationChangeModel(
                OrganizationChangeModel.class.getTypeName(),
                action,
                orgId,
                UserContext.getCorrelationId());

        //When you’re ready to publish the message, use the send() method on the
        //MessageChannel class returned from the source.output() method. Use instance of 
        //OrganizationChangeModel as the payload.
        source.output().send(MessageBuilder.withPayload(change).build());
        
        //The configuration that does the mapping of your service’s Spring Cloud Stream Source 
        //to a Kafka message broker and a message topic in Kafka done on organization-service 
        //bootstrap.yml and application.yml
    }
}
