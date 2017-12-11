package com.rbc.cloud.hackathon.kafka.producer.service;

import com.rbc.cloud.hackathon.data.Cities;
import com.rbc.cloud.hackathon.data.Customers;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.UUID;

@Component
public class KafkaCustomersProducer {
    private Logger logger= LoggerFactory.getLogger(KafkaCustomersProducer.class);

    @Autowired
    private Producer<String, Customers> customersProducer;

    @Value("${topic.name.customers}")
    String customersTopicName;

    @Value("${file.path.customers}")
    String customersFilePath;

    @PostConstruct
    private void publish() throws InterruptedException {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                logger.info("opening {}", customersFilePath);
                InputStreamReader isr = new InputStreamReader(getClass().getClassLoader().getResourceAsStream(customersFilePath));
                BufferedReader br = new BufferedReader(isr);

                try {
                    int i=0;
                    String line=null;
                    while ((line = br.readLine()) != null) {
                        i++;
                        try {
                            logger.info("processing customers line {} - {}", i, line);
                            String[] columns = line.split(",");
                            Customers custies = new Customers();
                            custies.setCustId(columns[0]);
                            custies.setFirstName(columns[1]);
                            custies.setLastName(columns[2]);
                            custies.setCityId(columns[3]);
                            ProducerRecord<String, Customers> producerRecord = new ProducerRecord<String, Customers>(customersTopicName, custies.getCustId().toString(), custies);
                            customersProducer.send(producerRecord);
                            logger.info("published customers line {}", i);
                            Thread.sleep(500);
                        }
                        catch (Exception e) {
                            logger.error("Error trying to process line {} - {}, error is {}",i,line,e.getMessage());
                            e.printStackTrace();
                        }

                    }
                    br.close();
                }
                catch (Exception e) {
                    logger.error("Error trying to read {} - error is {}, quitting", customersFilePath, e.getMessage());
                }

            }
        });

        t.start();

    }
}
