package com.iot.nero.nraft.entity;

import java.io.Serializable;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/7/17
 * Time   6:49 PM
 */
public class RequestVoteReply implements Serializable {
    private Integer term;
    private Boolean voteGranted;

    public RequestVoteReply() {
    }

    public RequestVoteReply(Integer term, Boolean voteGranted) {
        this.term = term;
        this.voteGranted = voteGranted;
    }

    public Integer getTerm() {
        return term;
    }

    public void setTerm(Integer term) {
        this.term = term;
    }

    public Boolean getVoteGranted() {
        return voteGranted;
    }

    public void setVoteGranted(Boolean voteGranted) {
        this.voteGranted = voteGranted;
    }

    @Override
    public String toString() {
        return "RequestVoteReply{" +
                "term=" + term +
                ", voteGranted=" + voteGranted +
                '}';
    }
}
