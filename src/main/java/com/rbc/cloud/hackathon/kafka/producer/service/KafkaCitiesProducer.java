package com.rbc.cloud.hackathon.kafka.producer.service;

import com.rbc.cloud.hackathon.data.Cities;
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
import java.io.InputStreamReader;
import java.util.Scanner;

@Component
public class KafkaCitiesProducer {
    private Logger logger= LoggerFactory.getLogger(KafkaCitiesProducer.class);

    @Autowired
    private Producer<String, Cities> citiesProducer;

    @Value("${topic.name.cities}")
    String topicName;

    @Value("${file.path.cities}")
    String citiesFilePath;

    @PostConstruct
    private void publish() throws InterruptedException {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                logger.info("opening {}", citiesFilePath);
                InputStreamReader isr = new InputStreamReader(getClass().getClassLoader().getResourceAsStream(citiesFilePath));
                BufferedReader br = new BufferedReader(isr);
                String line=null;
                try {
                    int i=0;
                    while ((line = br.readLine()) != null) {
                        i++;
                        try {
                            logger.info("processing cities line {} - {}", i, line);
                            String[] columns = line.split(",");
                            Cities cities = new Cities();
                            cities.setCityId(columns[0]);
                            cities.setCityName(columns[1]);
                            ProducerRecord<String, Cities> producerRecord = new ProducerRecord<String, Cities>(topicName, cities.getCityId().toString(), cities);
                            citiesProducer.send(producerRecord);
                            logger.info("published cities line {}", i);
                            Thread.sleep(500);
                        }
                        catch (Exception e) {
                            logger.error("Error trying to process cities line {} - {}, error is {}",i,line,e.getMessage());
                            e.printStackTrace();
                        }
                    }
                    br.close();
                }
                catch (Exception e) {
                    logger.error("Error trying to read {} - error is {}, quitting", citiesFilePath, e.getMessage());
                }
            }
        });

        t.start();

    }
}
