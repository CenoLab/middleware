package com.iot.nero.nraft.config;


import com.iot.nero.nraft.annotation.ConfigClass;
import com.iot.nero.nraft.annotation.ConfigField;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/6/4
 * Time   下午12:49
 */

@ConfigClass
public class Config {



    @ConfigField("server.host")
    private String host = "localhost";
    @ConfigField("server.port")
    private Integer port = 1080;





    @ConfigField("node.name")
    private String nodeName;
    @ConfigField("node.note")
    private String nodeNote;


    @ConfigField("auth.enable")
    private Boolean auth;
    @ConfigField("auth.key")
    private String authKey;
    @ConfigField("auth.secret")
    private String authSecret;

    public Config() {
    }

    public Config(String host, Integer port, String nodeName, String nodeNote, Boolean auth, String authKey, String authSecret) {
        this.host = host;
        this.port = port;
        this.nodeName = nodeName;
        this.nodeNote = nodeNote;
        this.auth = auth;
        this.authKey = authKey;
        this.authSecret = authSecret;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getNodeNote() {
        return nodeNote;
    }

    public void setNodeNote(String nodeNote) {
        this.nodeNote = nodeNote;
    }

    public Boolean getAuth() {
        return auth;
    }

    public void setAuth(Boolean auth) {
        this.auth = auth;
    }

    public String getAuthKey() {
        return authKey;
    }

    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }

    public String getAuthSecret() {
        return authSecret;
    }

    public void setAuthSecret(String authSecret) {
        this.authSecret = authSecret;
    }

    @Override
    public String toString() {
        return "Config{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", nodeName='" + nodeName + '\'' +
                ", nodeNote='" + nodeNote + '\'' +
                ", auth=" + auth +
                ", authKey='" + authKey + '\'' +
                ", authSecret='" + authSecret + '\'' +
                '}';
    }
}
