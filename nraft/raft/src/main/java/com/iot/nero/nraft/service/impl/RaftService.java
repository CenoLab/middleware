package com.iot.nero.nraft.service.impl;

import com.iot.nero.nraft.entity.*;
import com.iot.nero.nraft.service.IRaftService;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/7/19
 * Time   12:27 PM
 */
public class RaftService implements IRaftService {

    @Override
    public VoteReply vote(VoteArgs voteArgs) {
        return null;
    }

    @Override
    public void heartBeat(HeartBeat heartBeat) {

    }

    @Override
    public AppendEntriesReply logReplicate(AppendEntriesArgs appendEntriesArgs) {
        return null;
    }

    @Override
    public void logCommit() {

    }
}
