package com.iot.nero.nraft;


import com.iot.nero.nraft.annotation.InjectClass;
import com.iot.nero.nraft.annotation.Service;
import com.iot.nero.nraft.constant.CONSTANT;
import com.iot.nero.nraft.core.RaftTimeStateMachine;
import com.iot.nero.nraft.factory.ConfigFactory;
import com.iot.nero.nraft.server.IServer;
import com.iot.nero.nraft.server.RaftServer;
import com.iot.nero.nraft.utils.ClassUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.iot.nero.nraft.constant.CONSTANT.printNRaftInfo;
import static com.iot.nero.nraft.utils.System.checkSystem;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/6/4
 * Time   下午2:33
 */
@InjectClass
public class NRaftBootstrap {

    private Integer DFS_SERVER_LISTEN_PORT = 8849;


    private Map<Class<?>,Class<?>> classClassMap;

    List<Class<?>> clsList;



    private RaftTimeStateMachine raftTimeStateMachine;


    private void initStateMachine() throws NoSuchMethodException, InstantiationException, IOException, IllegalAccessException {
        raftTimeStateMachine = new RaftTimeStateMachine();
        raftTimeStateMachine.init();
    }


    private void initService()  {
        classClassMap = new HashMap<>();// 服务容器
        clsList = ClassUtil.getAllClassByPackageName(App.class.getPackage()); // 服务扫描

        for(Class<?> s:clsList){
            Service at = s.getAnnotation(Service.class);
            if (at!= null) {
                Class<?> interfaces[] = s.getInterfaces();//获得实现的所有接口
                for (Class<?> inter : interfaces) {
                    CONSTANT.pInfo("(Service) ["+s.getName()+"]<-["+inter.getName()+"]");
                    classClassMap.put(inter, s);// 服务注册
                }
            }
        }
    }

    private void startRaftStateMachine() throws InterruptedException{
        Thread raftTimeStateMachineThrad = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    raftTimeStateMachine.start();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        raftTimeStateMachineThrad.start();
    }

    private void runRaftListener() throws IOException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        DFS_SERVER_LISTEN_PORT = ConfigFactory.getConfig().getPort();
        final IServer raftServer = new RaftServer(classClassMap,DFS_SERVER_LISTEN_PORT);
        Thread raftServerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                raftServer.start();
            }
        });
        raftServerThread.start();

    }

    public void start() {
        printNRaftInfo();
        checkSystem();
        try {
            initStateMachine();
        } catch ( NoSuchMethodException | InstantiationException | IllegalAccessException | IOException e) {
            e.printStackTrace();
        }


        initService();


        try {
            runRaftListener();
        } catch (IOException | IllegalAccessException | NoSuchMethodException | InstantiationException e) {
            e.printStackTrace();
        }


        try {
            startRaftStateMachine();
        } catch (InterruptedException  e) {
            e.printStackTrace();
        }

    }


}
