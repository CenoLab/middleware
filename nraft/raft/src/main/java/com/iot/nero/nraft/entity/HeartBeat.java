package com.iot.nero.nraft.entity;

import com.iot.nero.nraft.cluster.entity.Node;

import java.io.Serializable;
import java.util.List;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/7/20
 * Time   1:59 PM
 */
public class HeartBeat implements Serializable {

    private Integer term;
    private List<Node> nodeList;

    public HeartBeat() {
    }

    public HeartBeat(Integer term, List<Node> nodeList) {
        this.term = term;
        this.nodeList = nodeList;
    }

    public Integer getTerm() {
        return term;
    }

    public void setTerm(Integer term) {
        this.term = term;
    }

    public List<Node> getNodeList() {
        return nodeList;
    }

    public void setNodeList(List<Node> nodeList) {
        this.nodeList = nodeList;
    }

    @Override
    public String toString() {
        return "HeartBeat{" +
                "term=" + term +
                ", nodeList=" + nodeList +
                '}';
    }
}
