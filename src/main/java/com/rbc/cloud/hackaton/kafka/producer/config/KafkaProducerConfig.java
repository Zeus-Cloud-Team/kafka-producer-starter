package com.rbc.cloud.hackaton.kafka.producer.config;


import com.rbc.cloud.hackaton.kafka.producer.util.JavaVersion;
import com.rbc.cloud.hackaton.kafka.producer.util.Util;
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
    Producer<String,String> producer() throws IOException {
        final Properties props = new Properties();

        if (exists("kafka.username") && exists("kafka.password")) {
            logger.info("Found credentials");
        } else {
            logger.error("either kafka.username or kafka.password is not set, please set both and rerun");
            System.exit(1);
        }

        String javaVersion=System.getProperty("java.version");
        logger.info("Java version {}, minimum required is 1.8.0_101", javaVersion);
        if (Util.checkJavaVersion(new JavaVersion(javaVersion), new JavaVersion("1.8.0_101"))) {
            logger.info("Java version is fine!");
        } else {
            logger.error("Incompatible Java version! Please upgrade java to be at least 1.8.0_101");
            System.exit(1);
        }

        String writableDir=null;
        if (exists("writable.dir") && new File(env.getProperty("writable.dir")).canWrite()) {
            writableDir=env.getProperty("writable.dir");
        }
        else  {
            logger.error("Please provide a dir path in application property writable.dir that can be written to");
            System.exit(1);
        }

        try {
            props.put("client.id", InetAddress.getLocalHost().getHostName());
        } catch (Exception e) {
            logger.error("Could not set client.id - {}",e.getMessage());
        }

        String jaasFile=null;

        try {
            jaasFile=Util.writeJaasFile(new File(env.getProperty("writable.dir")), env.getProperty("kafka.username"), env.getProperty("kafka.password"));
        }
        catch (Exception e) {
            logger.error("Error trying to write Jaas file - {}", e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }


        props.put("bootstrap.servers", env.getProperty("kafka.bootstrap.servers") );
        props.put("key.serializer", StringSerializer.class.getName() );
        props.put("value.serializer", StringSerializer.class.getName() );
        props.put("sasl.jaas.config", env.getProperty("sasl.jaas.config") );
        props.put("sasl.mechanism", env.getProperty("sasl.mechanism") );
        props.put("security.protocol", env.getProperty("security.protocol") );

        System.setProperty("java.security.auth.login.config",resourceLoader.getResource("file:/"+jaasFile).getURI().toString() );

        return new KafkaProducer<String, String>(props);
    }

}
