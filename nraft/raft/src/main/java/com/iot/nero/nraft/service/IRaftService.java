package com.iot.nero.nraft.service;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/7/19
 * Time   12:22 PM
 */
public interface IRaftService {

    /**
     * 选举
     */
    void vote();

    /**
     * 心跳
     */
    void heartBeat();

    /**
     * 数据发送 inconsistencies
     */
    void logReplicate();

    /**
     * 数据提交  overwrites inconsistencies
     */
    void logCommit();
    
}
