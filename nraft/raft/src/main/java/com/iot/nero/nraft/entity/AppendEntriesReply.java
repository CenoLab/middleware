package com.iot.nero.nraft.entity;

import java.io.Serializable;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/7/17
 * Time   6:51 PM
 */
public class AppendEntriesReply implements Serializable {

    private Integer term;
    private Boolean success;

    public AppendEntriesReply() {
    }

    public AppendEntriesReply(Integer term, Boolean success) {
        this.term = term;
        this.success = success;
    }

    public Integer getTerm() {
        return term;
    }

    public void setTerm(Integer term) {
        this.term = term;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "AppendEntriesReply{" +
                "term=" + term +
                ", success=" + success +
                '}';
    }
}
