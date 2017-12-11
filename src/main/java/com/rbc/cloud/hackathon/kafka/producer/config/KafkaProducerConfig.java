package com.rbc.cloud.hackathon.kafka.producer.config;


import com.rbc.cloud.hackathon.data.Cities;
import com.rbc.cloud.hackathon.kafka.producer.util.JavaVersion;
import com.rbc.cloud.hackathon.kafka.producer.util.Util;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
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
import java.io.File;
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

    private Boolean exists(String propertyName) {
        try {
            String val = env.getProperty(propertyName);
            return val != null && !val.equals("");
        } catch(Exception e) {
            return false;
        }
    }

    @Bean
    Producer<String,Cities> citiesProducer() throws IOException {
        final Properties props = new Properties();

        if (exists("kafka.username") && exists("kafka.password")) {
            logger.info("Found credentials");
        } else {
            String message="either kafka.username or kafka.password is not set, please set both and rerun";
            logger.error(message);
            throw new RuntimeException(message);
        }

        String javaVersion=System.getProperty("java.version");
        logger.info("Java version {}, minimum required is 1.8.0_101", javaVersion);

        JavaVersion myJavaVersion=new JavaVersion(javaVersion);
        JavaVersion minimumJavaVersion=new JavaVersion("1.8.0_101");

        if (myJavaVersion.greaterThan(minimumJavaVersion)) {
            logger.info("Java version is fine!");
        } else {
            String message="Incompatible Java version! Please upgrade java to be at least 1.8.0_101, you are at "+javaVersion;
            logger.error(message);
            throw new RuntimeException(message);
        }

        String writableDir=null;
        if (exists("writable.dir") && new File(env.getProperty("writable.dir")).canWrite()) {
            writableDir=env.getProperty("writable.dir");
        }
        else  {
            String message="Please provide a dir path in application property writable.dir that can be written to";
            logger.error(message);
            throw new RuntimeException(message);
        }

        try {
            props.put("client.id", InetAddress.getLocalHost().getHostName());
        } catch (Exception e) {
            logger.error("Could not set client.id - {}",e.getMessage());
        }

        String jaasFile=null;

        try {
            jaasFile=Util.writeJaasFile(new File(writableDir), env.getProperty("kafka.username"), env.getProperty("kafka.password"));
        }
        catch (Exception e) {
            logger.error("Error trying to write Jaas file - {}", e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        props.put("bootstrap.servers", env.getProperty("kafka.bootstrap.servers") );
        props.put("key.serializer", StringSerializer.class.getName() );
        props.put("value.serializer", KafkaAvroSerializer.class.getName());
        props.put("sasl.jaas.config", env.getProperty("sasl.jaas.config") );
        props.put("sasl.mechanism", env.getProperty("sasl.mechanism") );
        props.put("security.protocol", env.getProperty("security.protocol") );

        props.put("schema.registry.url", env.getProperty("schema.registry.url") );

        System.setProperty("java.security.auth.login.config",resourceLoader.getResource("file:/"+jaasFile).getURI().toString() );

        return new KafkaProducer<>(props);
    }

}
