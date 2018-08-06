package com.iot.nero.nraft.client;

import com.iot.nero.nraft.cluster.entity.Node;
import com.iot.nero.nraft.entity.response.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.iot.nero.nraft.constant.CONSTANT.pInfo;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/7/19
 * Time   12:47 PM
 */
public class RpcClientConnectionPool {

    private static Map<Node,RpcClient> nodeRpcClientMap = new HashMap<>();

    public RpcClient getConnection(Node node) throws NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
        RpcClient rpcClient = nodeRpcClientMap.get(node);
        if(rpcClient==null){
            rpcClient = new RpcClient(node);
            pInfo("(NEW CONNECTION) "+node.toString());
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

                @Override
                public void onConnectionException(Exception e) {
                    pInfo("(CONNECTION EXCEPTION) "+e);
                }
            });

            nodeRpcClientMap.put(node,rpcClient);

            return rpcClient;
        }
        return rpcClient;
    }

    public RpcClient getConnection(Node node,RpcErrorListener rpcErrorListener) throws NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
        RpcClient rpcClient = nodeRpcClientMap.get(node);
        if(rpcClient==null){
            rpcClient = new RpcClient(node);
            pInfo("(NEW CONNECTION) "+node.toString());
            rpcClient.init();
            // 错误事件监听
            rpcClient.setRpcErrorListener(rpcErrorListener);

            nodeRpcClientMap.put(node,rpcClient);

            return rpcClient;
        }
        return rpcClient;
    }
}
