package com.iot.nero.nraft.entity;

import java.io.Serializable;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/7/17
 * Time   6:49 PM
 */
public class HeartBeatReply implements Serializable {
    private Integer term;

    public HeartBeatReply() {
    }

    public HeartBeatReply(Integer term) {
        this.term = term;
    }

    public Integer getTerm() {
        return term;
    }

    public void setTerm(Integer term) {
        this.term = term;
    }

    @Override
    public String toString() {
        return "HeartBeatReply{" +
                "term=" + term +
                '}';
    }
}
