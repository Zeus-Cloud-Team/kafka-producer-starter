package com.rbc.cloud.hackathon.kafka.producer.service;

import com.rbc.cloud.hackathon.data.Cities;
import com.rbc.cloud.hackathon.data.Customers;
import com.rbc.cloud.hackathon.data.Transactions;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sun.java2d.loops.GraphicsPrimitive;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.InputStream;
import java.util.Scanner;
import java.util.UUID;

@Component
public class KafkaTransactionsProducer {
    private Logger logger= LoggerFactory.getLogger(KafkaTransactionsProducer.class);

    @Autowired
    private Producer<String, Transactions> transactionsProducer;

    @Value("${topic.name.transactions}")
    String transactionsTopicName;

    @Value("${file.path.transactions}")
    String transactionsFilePath;

    @PostConstruct
    private void publish() throws InterruptedException {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                logger.info("opening {}", transactionsFilePath);
                File custiesFile = new File(getClass().getClassLoader().getResource(transactionsFilePath).getFile());
                try {
                    int i=0;
                    Scanner scanner = new Scanner(custiesFile);
                    while (scanner.hasNextLine()) {
                        i++;
                        String line = scanner.nextLine();
                        try {
                            logger.info("processing transactions line {} - {}", i, line);
                            String[] columns = line.split(",");
                            Transactions txn=new Transactions();
                            txn.setTransactionId(columns[0]);
                            txn.setCustId(columns[1]);
                            txn.setTransactionType(columns[2]);
                            txn.setTransactionAmount(columns[3]);
                            ProducerRecord<String, Transactions> producerRecord = new ProducerRecord<String, Transactions>(transactionsTopicName, txn.getTransactionId().toString(), txn);
                            transactionsProducer.send(producerRecord);
                            logger.info("published transactions line {}", i);
                            Thread.sleep(500);
                        }
                        catch (Exception e) {
                            logger.error("Error trying to process transactions line {} - {}, error is {}",i,line,e.getMessage());
                            e.printStackTrace();
                        }
                    }
                    scanner.close();
                }
                catch (Exception e) {
                    logger.error("Error trying to read {} - error is {}, quitting", transactionsFilePath, e.getMessage());
                }
            }
        });

        t.start();

    }
}
