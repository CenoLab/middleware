package com.iot.nero.nraft.service;

import com.iot.nero.nraft.entity.*;

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
    VoteReply vote(VoteArgs voteArgs);

    /**
     * 心跳
     */
    void heartBeat(HeartBeat heartBeat);

    /**
     * 数据发送 inconsistencies
     */
    AppendEntriesReply logReplicate(AppendEntriesArgs appendEntriesArgs);

    /**
     * 数据提交  overwrites inconsistencies
     */
    void logCommit();
    
}
