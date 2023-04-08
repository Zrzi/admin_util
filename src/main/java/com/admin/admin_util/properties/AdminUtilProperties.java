package com.admin.admin_util.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 陈群矜
 */
@ConfigurationProperties(prefix = "admin.util")
public class AdminUtilProperties {

    private boolean enabled;

    private String ip = "127.0.0.1";

    private int port = 8000;

    public AdminUtilProperties() {}

    public boolean getEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

}
