package com.iot.nero.nraft;


import com.iot.nero.nraft.annotation.Service;
import com.iot.nero.nraft.constant.CONSTANT;
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
public class NRaftBootstrap {

    private Integer DFS_SERVER_LISTEN_PORT = 8849;


    Map<Class<?>,Class<?>> classClassMap;




    public void initService(){
        classClassMap = new HashMap<>();// 服务容器
        List<Class<?>> clsList = ClassUtil.getAllClassByPackageName(App.class.getPackage()); // 服务扫描

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

    public void runRaftListener() throws IOException {
        IServer ndfsServer = new RaftServer(classClassMap,DFS_SERVER_LISTEN_PORT);
        ndfsServer.start();
    }

    public void start() {
        printNRaftInfo();
        checkSystem();

        initService();
        try {
            runRaftListener();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
