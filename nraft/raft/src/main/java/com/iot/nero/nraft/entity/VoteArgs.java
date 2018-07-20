package com.iot.nero.nraft.entity;

import java.io.Serializable;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/7/17
 * Time   6:48 PM
 */
public class VoteArgs implements Serializable {
    private Integer term;
    private Integer candidateId;
    private Integer lastLogIndex;
    private Integer lastLogTerm;

    public VoteArgs() {
    }

    public VoteArgs(Integer term, Integer candidateId, Integer lastLogIndex, Integer lastLogTerm) {
        this.term = term;
        this.candidateId = candidateId;
        this.lastLogIndex = lastLogIndex;
        this.lastLogTerm = lastLogTerm;
    }

    public Integer getTerm() {
        return term;
    }

    public void setTerm(Integer term) {
        this.term = term;
    }

    public Integer getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(Integer candidateId) {
        this.candidateId = candidateId;
    }

    public Integer getLastLogIndex() {
        return lastLogIndex;
    }

    public void setLastLogIndex(Integer lastLogIndex) {
        this.lastLogIndex = lastLogIndex;
    }

    public Integer getLastLogTerm() {
        return lastLogTerm;
    }

    public void setLastLogTerm(Integer lastLogTerm) {
        this.lastLogTerm = lastLogTerm;
    }

    @Override
    public String toString() {
        return "VoteArgs{" +
                "term=" + term +
                ", candidateId=" + candidateId +
                ", lastLogIndex=" + lastLogIndex +
                ", lastLogTerm=" + lastLogTerm +
                '}';
    }
}
