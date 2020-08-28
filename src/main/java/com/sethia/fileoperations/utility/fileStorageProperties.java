package com.sethia.fileoperations.utility;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "file")
public class fileStorageProperties {
    private String serverDir;

    public String getServerDir() {
        return serverDir;
    }

    public void setServerDir(String serverDir) {
        this.serverDir = serverDir;
    }
}