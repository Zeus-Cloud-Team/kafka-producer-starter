package com.rbc.cloud.hackaton.kafka.producer.config;


import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Properties;

@Configuration
@PropertySource(value = "classpath:application.properties")
public class KafkaProducerConfig {
    private Logger logger = LoggerFactory.getLogger(KafkaProducerConfig.class);

    @Resource
    private Environment env;

    @Autowired
    private ResourceLoader resourceLoader;

    @Bean
    Producer<String,String> producer() throws IOException {
        final Properties props = new Properties();

        try {
            props.put("client.id", InetAddress.getLocalHost().getHostName());
        } catch (Exception e) {
            logger.error("Could not set client.id - {}",e.getMessage());
        }

        props.put("bootstrap.servers", env.getProperty("kafka.bootstrap.servers") );
        props.put("key.serializer", StringSerializer.class.getName() );
        props.put("value.serializer", StringSerializer.class.getName() );
        props.put("sasl.jaas.config", env.getProperty("sasl.jaas.config") );
        props.put("sasl.mechanism", env.getProperty("sasl.mechanism") );
        props.put("security.protocol", env.getProperty("security.protocol") );

        String jaasFile=env.getProperty("jaas.file");
        logger.info(jaasFile);

        System.setProperty("java.security.auth.login.config",resourceLoader.getResource(jaasFile).getURI().toString() );

        return new KafkaProducer<String, String>(props);
    }

}
