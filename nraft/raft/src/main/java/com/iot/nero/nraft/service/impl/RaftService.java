package com.iot.nero.nraft.service.impl;


import com.iot.nero.nraft.core.RaftTimeStateMachine;
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
        if(voteArgs.getTerm()<RaftTimeStateMachine.getTerm()){ //if term smaller than my term
            return new VoteReply(RaftTimeStateMachine.getTerm(),false); // reject
        }
        return new VoteReply(RaftTimeStateMachine.getTerm(),true); // vote
    }

    @Override
    public HeartBeatReply heartBeat(HeartBeat heartBeat) {

        // todo update new node list

        return new HeartBeatReply(RaftTimeStateMachine.getTerm());
    }

    @Override
    public void logReplicate() {

    }

    @Override
    public void logCommit() {

    }
}
