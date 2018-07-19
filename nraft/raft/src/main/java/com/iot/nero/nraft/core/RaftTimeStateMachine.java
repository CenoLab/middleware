package com.iot.nero.nraft.core;

import com.iot.nero.nraft.client.RpcClient;
import com.iot.nero.nraft.client.RpcClientConnectionPool;
import com.iot.nero.nraft.client.RpcErrorListener;
import com.iot.nero.nraft.cluster.entity.Node;
import com.iot.nero.nraft.constant.Role;
import com.iot.nero.nraft.entity.response.Response;
import com.iot.nero.nraft.service.IRaftService;
import com.iot.nero.nraft.utils.RandomUtils;

import java.io.IOException;
import java.util.ArrayList;
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

    private Integer term;
    private AtomicInteger voteNum;

    public static Role role = Role.FOLLOWER; // 当前节点状态
    public static Integer waitMilliseconds; // follower 等待时间

    private RpcClient rpcClient;


    RpcClientConnectionPool connectionPool = new RpcClientConnectionPool();

    // 并发请求线程池
    ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());


    public void init() throws NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
        voteNum = new AtomicInteger(0);

        rpcClient = RpcClient.getInstance();
        rpcClient.init();

        // 错误事件监听
        rpcClient.setRpcErrorListener(new RpcErrorListener() {
            @Override
            public void onError(Response<Object> response) {
                // 错误处理
                switch (response.getCode()){
                    case 1: // 未知的请求类型
                        System.out.println(response.getMsg());
                        break;
                    case 2: // 节点认证失败
                        System.out.println(response.getMsg());
                        break;
                    // todo ....
                }
            }
        });

        //todo 获取节点列表

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
    private void startElection() {
        voteNum = new AtomicInteger(0); // 将票数设置为0

        List<Node> nodeList = new ArrayList<>();

        for(Node node:nodeList){ // 遍历节点
            // 从客户端连接池获取连接
            final IRaftService raftService = connectionPool.getConnection(node).getRemoteProxyService(IRaftService.class);
            executorService.execute(new Runnable() { // 并发向其他节点发送投票请求
                @Override
                public void run() {
                    raftService.vote(); // todo 投票请求参数
                }
            });
        }

    }

    /**
     * 作为 leader 向其他节点发送心跳
     */
    private void sendHeartBeat(){

    }

    public void start() throws InterruptedException {

        startUp(); // 起始

        while (isRunning) {
            // 若在等待时间中 RPC 线程 收到 leader 的 heartbeat ， 则 RPC 线程更新 role 为 Follower

            if (role == Role.CANDIDATE) { // 状态变成 Candidate 并向其他节点发出 voteMe 请求
                startElection(); // 开始选举
            }

            if (role == Role.FOLLOWER) {
                waitHeartBeat();
            }

            if (role == Role.LEADER) {
                sendHeartBeat();
            }
        }
    }


}
