package com.iot.nero.nraft.client;

import com.iot.nero.nraft.cluster.NodeManager;
import com.iot.nero.nraft.cluster.entity.Node;
import com.iot.nero.nraft.constant.CONSTANT;
import com.iot.nero.nraft.entity.auth.Authentication;
import com.iot.nero.nraft.entity.request.InvokeEntity;
import com.iot.nero.nraft.entity.request.Request;
import com.iot.nero.nraft.entity.response.Response;
import com.iot.nero.nraft.exception.InvalidHostNameException;
import com.iot.nero.nraft.exception.InvalidPortException;
import com.iot.nero.nraft.factory.ConfigFactory;
import com.iot.nero.nraft.utils.ProtoStuffUtils;
import org.xerial.snappy.Snappy;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.UUID;

import static com.iot.nero.nraft.constant.CONSTANT.pInfo;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/7/19
 * Time   9:33 AM
 */
public class RpcClient {

    SocketChannel socketChannel;
    Selector selector;


    private RpcErrorListener rpcErrorListener;

    private String address = "";
    private int port = -1;

    private ByteBuffer buffer = ByteBuffer.allocate(1024);
    byte[] receivedBytes;


    public RpcClient(Node node) {
        this.address = node.getHost();
        this.port = node.getPort();
    }

    public void init() throws IOException {
        this.selector = Selector.open();
    }


    public <T> T getRemoteProxyService(final Class<?> interfaceClass) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        Request<InvokeEntity> request = new Request<>(); // 创建并初始化 RPC 请求
                        request.setRequestId(UUID.randomUUID().toString());

                        InvokeEntity invokeEntity = new InvokeEntity();
                        invokeEntity.setaClass(interfaceClass);
                        invokeEntity.setMethodName(method.getName());
                        invokeEntity.setParameterType(method.getParameterTypes());
                        invokeEntity.setArgs(args);

                        if (ConfigFactory.getConfig().getAuth()) {
                            Authentication authentication = new Authentication(
                                    ConfigFactory.getConfig().getAuthKey(),
                                    ConfigFactory.getConfig().getAuthSecret());
                            request.setAuthentication(authentication);
                        }

                        request.setRequestType((byte) 0x01);
                        request.setData(invokeEntity);


                        if ("".equals(address)) {
                            throw new InvalidHostNameException(CONSTANT.INVALID_HOST_NAME);
                        }
                        if (port < 0 || port > 65535) {
                            throw new InvalidPortException(CONSTANT.INVALID_PORT);
                        }
                        pInfo("(CONNECT) "+address+":"+port);

                        try {
                           socketChannel = SocketChannel.open(new InetSocketAddress(address, port));
                           socketChannel.configureBlocking(false);
                           socketChannel.write(ByteBuffer.wrap(Snappy.compress(ProtoStuffUtils.serializer(request))));
                           socketChannel.register(selector, SelectionKey.OP_READ);
                            return getResult();
                        }catch (ConnectException e){
                            rpcErrorListener.onException(e);
                            socketChannel.close();
                        }
                        return null;
                    }
                }
        );
    }

    private synchronized Object getResult() throws IOException {
        while (this.selector.select() > 0) {
            for (SelectionKey sk : this.selector.selectedKeys()) {
                this.selector.selectedKeys().remove(sk);
                if (sk.isReadable()) {
                    SocketChannel sc = (SocketChannel) sk.channel();
                    this.buffer.clear();
                    int readCount = sc.read(this.buffer);
                    if (readCount > 0) {
                        this.buffer.flip();
                        this.receivedBytes = new byte[readCount];
                        byte[] array = this.buffer.array();
                        System.arraycopy(array, 0, this.receivedBytes, 0, readCount);
                        Response<Object> response = ProtoStuffUtils.deserializer(Snappy.uncompress(receivedBytes), Response.class);
                        this.buffer.clear();
                        if (response.isStatus()) {
                            return response.getData();
                        }
                        if (rpcErrorListener != null) {
                            rpcErrorListener.onError(response); // 调用错误处理
                        }
                        pInfo("(Error) " + response.getMsg());
                        return null;
                    }
                }
            }
        }
        return null;
    }


    public RpcErrorListener getRpcErrorListener() {
        return rpcErrorListener;
    }

    public void setRpcErrorListener(RpcErrorListener rpcErrorListener) {
        this.rpcErrorListener = rpcErrorListener;
    }
}
