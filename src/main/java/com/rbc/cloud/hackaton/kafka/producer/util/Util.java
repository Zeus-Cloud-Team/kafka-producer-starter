package com.rbc.cloud.hackaton.kafka.producer.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;


public class Util {



    private static Logger logger = LoggerFactory.getLogger(Util.class);

    public static String writeJaasFile(File dir, String username, String password) throws IOException {
        if (!dir.canWrite()) {
            throw new IOException("Unable to write to directory provided");
        }
        String jaasFileContents="KafkaClient {\n" +
                "org.apache.kafka.common.security.plain.PlainLoginModule required\n" +
                "username=\""+username+"\"\n" +
                "password=\""+password+"\";\n" +
                "};";

        logger.info("writing this to the JAAS file - {}", jaasFileContents);

        String jaasPath = dir.getCanonicalPath()+"/jaas.conf";

        File jaasFile = new File(jaasPath);

        logger.info("Delete {} if it exists", jaasFile.toPath());

        Files.deleteIfExists(jaasFile.toPath());

        jaasFile.createNewFile();

        FileWriter writer = new FileWriter(jaasFile);

        writer.write(jaasFileContents);

        writer.close();

        logger.info("Done writing jaas file");

        return jaasPath;

    }

}
