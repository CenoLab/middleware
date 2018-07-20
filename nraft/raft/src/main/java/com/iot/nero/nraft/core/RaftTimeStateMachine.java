package com.iot.nero.nraft.core;

import com.iot.nero.nraft.client.RpcClientConnectionPool;
import com.iot.nero.nraft.cluster.NodeManager;
import com.iot.nero.nraft.cluster.entity.Node;
import com.iot.nero.nraft.constant.Role;
import com.iot.nero.nraft.entity.HeartBeat;
import com.iot.nero.nraft.entity.VoteArgs;
import com.iot.nero.nraft.service.IRaftService;
import com.iot.nero.nraft.utils.RandomUtils;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/7/19
 * Time   9:18 AM
 */
public class RaftTimeStateMachine {

    private Boolean isRunning = false;

    private static Integer term;
    private Integer candidateId;
    private Integer lastLogIndex;
    private Integer lastLogTerm;
    private AtomicInteger voteNum;



    private Role role = Role.FOLLOWER; // 当前节点状态
    private Integer waitMilliseconds; // follower 等待时间


    private NodeManager nodeManager;


    private RpcClientConnectionPool connectionPool = new RpcClientConnectionPool();

    // 并发请求线程池
    private ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());


    public void init() throws NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
        voteNum = new AtomicInteger(0);
        nodeManager = new NodeManager();

    }


    private void startUp() throws InterruptedException {
        Integer startWaitMilliseconds = RandomUtils.getRandom(100, 200);  // 随机 100ms～200ms 等待时长，等待leader heartbeat
        while (role == Role.FOLLOWER) {
            if (startWaitMilliseconds == 0) {
                role = Role.CANDIDATE; // 等待时间到，没收到响应，状态变成 Candidate
                break; // 跳出等待循环
            } else {
                startWaitMilliseconds -= 1;
                Thread.sleep(1);
            }
        }
    }

    private void waitHeartBeat() throws InterruptedException {
        while (role == Role.FOLLOWER) {
            if (waitMilliseconds == 0) {
                role = Role.CANDIDATE; // 等待时间到，没收到响应，状态变成 Candidate
                break; // 跳出等待循环
            } else {
                waitMilliseconds -= 1; // 过程中收到 heartBeat，RPC 线程修改等待时间
                Thread.sleep(1);
            }
        }
    }


    /**
     * 作为 candidate 向其他节点发送 选举请求
     */
    private void startElection() throws NoSuchMethodException, InstantiationException, IOException, IllegalAccessException {
        voteNum = new AtomicInteger(0); // 将票数设置为0

        final List<Node> nodeList = nodeManager.getNodeList();

        final VoteArgs voteArgs = new VoteArgs(term, candidateId, lastLogIndex, lastLogTerm);

        for (Node node : nodeList) { // 遍历节点
            // 从客户端连接池获取连接
            final IRaftService raftService = connectionPool.getConnection(node).getRemoteProxyService(IRaftService.class);
            executorService.execute(new Runnable() { // 并发向其他节点发送投票请求
                @Override
                public void run() {
                    if (raftService.vote(voteArgs).getVoteGranted()) { // 投票请求
                        if (voteNum.incrementAndGet() > nodeList.size() / 2) { // 的到一般以上的节点票数
                            role = Role.LEADER;
                        }
                    }
                }
            });
        }
    }

    /**
     * 作为 leader 向其他节点发送心跳
     */
    private void sendHeartBeat() throws NoSuchMethodException, InstantiationException, IOException, IllegalAccessException {
        List<Node> nodeList =  nodeManager.getNodeList();

        final HeartBeat heartBeat = new HeartBeat(term,nodeList);

        for(Node node:nodeList){ // 遍历节点
            // 从客户端连接池获取连接
            final IRaftService raftService = connectionPool.getConnection(node).getRemoteProxyService(IRaftService.class);
            executorService.execute(new Runnable() { // 并发向其他节点发送心跳
                @Override
                public void run() {
                    raftService.heartBeat(heartBeat); // 心跳发送
                }
            });
        }

    }

    public void start() throws InterruptedException {

        isRunning = true;

        startUp(); // 起始

        while (isRunning) {
            // 若在等待时间中 RPC 线程 收到 leader 的 heartbeat ， 则 RPC 线程更新 role 为 Follower

            if (role == Role.CANDIDATE) { // 状态变成 Candidate 并向其他节点发出 voteMe 请求
                try {
                    startElection(); // 开始选举
                } catch (NoSuchMethodException | InstantiationException | IOException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            if (role == Role.FOLLOWER) {
                waitHeartBeat();
            }

            if (role == Role.LEADER) try {
                sendHeartBeat();
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static Integer getTerm() {
        return term;
    }
}
