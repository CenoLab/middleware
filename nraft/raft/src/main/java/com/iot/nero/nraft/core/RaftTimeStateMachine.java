package com.iot.nero.nraft.core;

import com.iot.nero.nraft.client.RpcClientConnectionPool;
import com.iot.nero.nraft.cluster.NodeManager;
import com.iot.nero.nraft.cluster.entity.Node;
import com.iot.nero.nraft.constant.Role;
import com.iot.nero.nraft.entity.HeartBeat;
import com.iot.nero.nraft.entity.HeartBeatReply;
import com.iot.nero.nraft.entity.VoteArgs;
import com.iot.nero.nraft.factory.ConfigFactory;
import com.iot.nero.nraft.service.IRaftService;
import com.iot.nero.nraft.utils.RandomUtils;

import java.io.IOException;
import java.net.ConnectException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.iot.nero.nraft.constant.CONSTANT.pInfo;


/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/7/19
 * Time   9:18 AM
 */
public class RaftTimeStateMachine {

    private Boolean isRunning = false;

    private static Integer term = 0;
    private Integer candidateId;
    private Integer lastLogIndex;
    private Integer lastLogTerm;
    private AtomicInteger voteNum;


    private Map<Node,Integer> inActiveMap;



    private Role role = Role.FOLLOWER; // 当前节点状态
    private Integer waitMilliseconds = 15*1000; // follower 等待时间


    private NodeManager nodeManager;


    private RpcClientConnectionPool connectionPool = new RpcClientConnectionPool();

    // 并发请求线程池
    private ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());


    public void init() throws NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
        waitMilliseconds = ConfigFactory.getConfig().getHeartBeatTimeOut();

        voteNum = new AtomicInteger(0);
        nodeManager = new NodeManager();
        inActiveMap = new HashMap<>();
        initInActiveMap();
    }

    private void initInActiveMap() {
        for(Node node:nodeManager.getNodeList()){
            if(inActiveMap.containsKey(node)){
                inActiveMap.put(node,inActiveMap.get(node)+1);
            }else {
                inActiveMap.put(node, 0);
            }
        }
    }


    private void startUp() throws InterruptedException {

        //检查节点列表个数
        if(nodeManager.getNodeList().size()==0){ //只有自己一个节点，第一个启动的节点
            role = Role.LEADER; //自己就是Leader
        }

        Integer startWaitMilliseconds = RandomUtils.getRandom(100, 200);  // 随机 100ms～200ms 等待时长，等待leader heartbeat
        while (role == Role.FOLLOWER) {
            if (startWaitMilliseconds <= 0) {
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
            if (waitMilliseconds <= 0) {
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
    private void startElection() throws NoSuchMethodException, InstantiationException, IOException, IllegalAccessException, InterruptedException {
        voteNum = new AtomicInteger(0); // 将票数设置为0

        final List<Node> nodeList = nodeManager.getNodeList();

        final VoteArgs voteArgs = new VoteArgs(term, candidateId, lastLogIndex, lastLogTerm);
        final CountDownLatch latch = new CountDownLatch(nodeList.size());
        for (Node node : nodeList) { // 遍历节点
            // 从客户端连接池获取连接
            final IRaftService raftService = connectionPool.getConnection(node).getRemoteProxyService(IRaftService.class);
            executorService.execute(new Runnable() { // 并发向其他节点发送投票请求
                @Override
                public void run() {
                    if (raftService.vote(voteArgs).getVoteGranted()) { // 投票请求
                        latch.countDown();
                        if (voteNum.incrementAndGet() >= nodeList.size() / 2) {
                            for(int i = 0;i<nodeList.size();i++){
                                latch.countDown();
                            }
                        } // 的到一般以上的节点票数
                        role = Role.LEADER;
                    }
                }
            });
        }
        if(!latch.await(ConfigFactory.getConfig().getTimeOut(),TimeUnit.MILLISECONDS)){
            role = Role.FOLLOWER;
        }
    }

    /**
     * 作为 leader 向其他节点发送心跳
     */
    private void sendHeartBeat() throws NoSuchMethodException, InstantiationException, IOException, IllegalAccessException, InterruptedException {
        List<Node> nodeList =  nodeManager.getNodeList();

        initInActiveMap();
        final HeartBeat heartBeat = new HeartBeat(term,nodeList);
        final CountDownLatch latch = new CountDownLatch(nodeList.size());

        for(final Node node:nodeList){ // 遍历节点
            // 从客户端连接池获取连接
            final IRaftService raftService = connectionPool.getConnection(node).getRemoteProxyService(IRaftService.class);
            executorService.execute(new Runnable() { // 并发向其他节点发送心跳
                @Override
                public void run() {
                    HeartBeatReply heartBeatReply  = raftService.heartBeat(heartBeat); // 心跳发送
                    inActiveMap.remove(node);

                    if(heartBeatReply.getTerm()>RaftTimeStateMachine.term){ // 如果term大于当前term，集群可能产生了脑裂
                        role = Role.FOLLOWER;
                        // todo follow new leader.
                    }

                    latch.countDown();
                }
            });
        }

        if (!latch.await(ConfigFactory.getConfig().getTimeOut(), TimeUnit.MILLISECONDS)) { // 某个节点超时未响应
            pInfo("some node timeout,em...");
            // 节点超时次数加一
            for(Map.Entry<Node,Integer> entry:inActiveMap.entrySet()){
                inActiveMap.put(entry.getKey(),entry.getValue()+1);
                if(entry.getValue()>=5){
                    nodeManager.removeNode(entry.getKey());
                }
            }
            // 高并发情况下的业务流量导致心跳命令包丢包率增大，还得考虑心跳未响应次数判断节点是否掉线
            // 如果某个节点连续超时5次 , 就从NodeManager里删除
        }
        Thread.sleep(ConfigFactory.getConfig().getHeartBeatInterval());

    }

    public void start() throws InterruptedException {

        isRunning = true;

        startUp(); // 起始

        while (isRunning) {
            // 若在等待时间中 RPC 线程 收到 leader 的 heartbeat ， 则 RPC 线程更新 role 为 Follower

            if (role == Role.CANDIDATE) { // 状态变成 Candidate 并向其他节点发出 voteMe 请求
                try {
                    startElection(); // 开始选举
                }catch (NoSuchMethodException | InstantiationException | IOException | IllegalAccessException e) {
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
