package com.rbc.cloud.hackaton.kafka.producer.util;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaVersion {
    private Logger logger = LoggerFactory.getLogger(JavaVersion.class);
    int major;
    int minor;
    int revision;
    int update;
    String javaVersionString;

    public JavaVersion(String version) {
        this.javaVersionString=version;
        try {
            String[] versions = version.split("\\.");

            major = Integer.parseInt(versions[0]);
            minor = Integer.parseInt(versions[1]);
            String[] revisionupdate = versions[2].split("_");
            revision = Integer.parseInt(revisionupdate[0]);
            update = Integer.parseInt(revisionupdate[1]);
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

    public Boolean greaterThan(JavaVersion minimumVersion) {

        logger.info("Compare {} > {}", this, minimumVersion);
        try {
            Integer major = this.getMajor();
            Integer minor = this.getMinor();
            Integer revision = this.getRevision();
            Integer update = this.getUpdate();

            if (minor == minimumVersion.getMinor()) {
                if (revision > minimumVersion.getRevision()) {
                    return true;
                }
                else if(revision==minimumVersion.getRevision() && update >= minimumVersion.getUpdate()) {
                    return true;
                }
            }
            return false;

        } catch ( Exception e) {
            logger.error("Exception trying to parse java version - {}", e.getMessage());
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.DEFAULT_STYLE);
    }
}