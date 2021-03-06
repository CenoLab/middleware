package com.iot.nero.nraft.cluster;

import com.iot.nero.nraft.cluster.entity.Node;
import com.iot.nero.nraft.factory.ConfigFactory;
import com.sun.jna.platform.win32.Winnetwk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.iot.nero.nraft.constant.CONSTANT.pInfo;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/7/19
 * Time   12:46 PM
 */
public class NodeManager {
    private Node leader;
    private List<Node> nodeList;


    public NodeManager() throws NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
        nodeList = new ArrayList<>();
        String defaultNodeList = ConfigFactory.getConfig().getNodeList();
        if (defaultNodeList != null) { // 配置文件里面没有其他节点列表配置
            defaultNodeList = defaultNodeList.replaceAll("\\s*", "");
            String[] nodeListString = defaultNodeList.split(";");
            for (String nodeString : nodeListString) {
                String[] nodeInfo = nodeString.split(":");
                nodeList.add(new Node("", nodeInfo[0], Integer.valueOf(nodeInfo[1])));
            }
        }
        pInfo("(NODES) "+nodeList.toString());
    }

    public List<Node> addNode(Node node) {
        nodeList.add(node);
        return nodeList;
    }

    public List<Node> getNodeList() {
        return nodeList;
    }

    public Node changeLeader(Node node) {
        this.leader = node;
        return this.leader;
    }

    public List<Node> removeNode(Node node) {
        nodeList.remove(node);
        return nodeList;
    }
}
