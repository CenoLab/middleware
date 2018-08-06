package com.iot.nero.nraft.config;


import com.iot.nero.nraft.annotation.ConfigClass;
import com.iot.nero.nraft.annotation.ConfigField;
import com.iot.nero.nraft.annotation.InjectField;

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


    @ConfigField("cluster.default.node.list")
    private String nodeList;

    @ConfigField("cluster.default.node.timeout")
    private Integer timeOut;

    @ConfigField("cluster.default.leader.heartbeat.interval")
    private Integer heartBeatInterval;

    @ConfigField("cluster.default.follower.heartbeat.timeout")
    private Integer heartBeatTimeOut;



    @ConfigField("auth.enable")
    private Boolean auth;
    @ConfigField("auth.key")
    private String authKey;
    @ConfigField("auth.secret")
    private String authSecret;

    public Config() {
    }

    public Config(String host, Integer port, String nodeName, String nodeNote, String nodeList, Integer timeOut, Integer heartBeatInterval, Integer heartBeatTimeOut, Boolean auth, String authKey, String authSecret) {
        this.host = host;
        this.port = port;
        this.nodeName = nodeName;
        this.nodeNote = nodeNote;
        this.nodeList = nodeList;
        this.timeOut = timeOut;
        this.heartBeatInterval = heartBeatInterval;
        this.heartBeatTimeOut = heartBeatTimeOut;
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

    public String getNodeList() {
        return nodeList;
    }

    public void setNodeList(String nodeList) {
        this.nodeList = nodeList;
    }

    public Integer getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(Integer timeOut) {
        this.timeOut = timeOut;
    }

    public Integer getHeartBeatInterval() {
        return heartBeatInterval;
    }

    public void setHeartBeatInterval(Integer heartBeatInterval) {
        this.heartBeatInterval = heartBeatInterval;
    }

    public Integer getHeartBeatTimeOut() {
        return heartBeatTimeOut;
    }

    public void setHeartBeatTimeOut(Integer heartBeatTimeOut) {
        this.heartBeatTimeOut = heartBeatTimeOut;
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
                ", nodeList='" + nodeList + '\'' +
                ", timeOut=" + timeOut +
                ", heartBeatInterval=" + heartBeatInterval +
                ", heartBeatTimeOut=" + heartBeatTimeOut +
                ", auth=" + auth +
                ", authKey='" + authKey + '\'' +
                ", authSecret='" + authSecret + '\'' +
                '}';
    }
}
