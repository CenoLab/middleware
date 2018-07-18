package com.iot.nero.nraft.entity;

import java.io.Serializable;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/7/17
 * Time   6:50 PM
 */
public class AppendEntriesArgs implements Serializable {
    private Integer term;
    private Integer leaderId;

    public AppendEntriesArgs() {
    }

    public AppendEntriesArgs(Integer term, Integer leaderId) {
        this.term = term;
        this.leaderId = leaderId;
    }

    public Integer getTerm() {
        return term;
    }

    public void setTerm(Integer term) {
        this.term = term;
    }

    public Integer getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(Integer leaderId) {
        this.leaderId = leaderId;
    }

    @Override
    public String toString() {
        return "AppendEntriesArgs{" +
                "term=" + term +
                ", leaderId=" + leaderId +
                '}';
    }
}
