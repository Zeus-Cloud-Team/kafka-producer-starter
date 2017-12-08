package com.rbc.cloud.hackaton.kafka.producer.service;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.UUID;

@Component
public class KafkaProducer {
    private Logger logger= LoggerFactory.getLogger(KafkaProducer.class);

    @Autowired
    private Producer<String, String> producer;

    private String getUniqueKey() {
        return String.valueOf(System.currentTimeMillis());
    }

    private String getValue() {
        return UUID.randomUUID().toString();
    }

    @PostConstruct
    private void publish() throws InterruptedException {
        while (true) {
            String key=getUniqueKey();
            String value=getValue();
            logger.info("About to publish key / value = {} / {} ", key, value);
            ProducerRecord<String, String> producerRecord = new ProducerRecord<String, String>("lojo-test-topic-1",key,value);
            producer.send(producerRecord);
            logger.info("Published!  Wait 2 seconds the proceed to next iteration");
            Thread.sleep(2000);
        }

    }
}
