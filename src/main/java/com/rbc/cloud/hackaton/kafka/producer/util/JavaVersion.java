package com.rbc.cloud.hackaton.kafka.producer.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaVersion {
    private Logger logger = LoggerFactory.getLogger(JavaVersion.class);
    int major;
    int minor;
    int revision;
    int update;

    public JavaVersion(String version) {
        try {
            String[] versions = version.split("\\.");

            Integer major = Integer.parseInt(versions[0]);
            Integer minor = Integer.parseInt(versions[1]);
            String[] revisionupdate = versions[2].split("_");
            Integer revision = Integer.parseInt(revisionupdate[0]);
            Integer update = Integer.parseInt(revisionupdate[1]);
        } catch (Exception e) {
            logger.error("Unable to parse a version of the passed in version tea");
            throw e;
        }

    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public int getRevision() {
        return revision;
    }

    public void setRevision(int revision) {
        this.revision = revision;
    }

    public int getUpdate() {
        return update;
    }

    public void setUpdate(int update) {
        this.update = update;
    }
}