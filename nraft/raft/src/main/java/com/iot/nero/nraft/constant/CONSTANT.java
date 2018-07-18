package com.iot.nero.nraft.constant;

import java.util.Date;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/6/5
 * Time   下午3:37
 */
public class CONSTANT {
    public static final String VERSION = "0.2.3";
    public static final String COPY_RIGHT = "www.cenocloud.com All Right Reserved";
    public static final String CLIENT_OFFLINE = "客户端掉线";

    public static void printNRaftInfo() {
        System.out.println("███╗   ██╗██████╗ ███████╗███████╗");
        System.out.println("████╗  ██║██╔══██╗██╔════╝██╔════╝");
        System.out.println("██╔██╗ ██║██║  ██║█████╗  ███████╗");
        System.out.println("██║╚██╗██║██║  ██║██╔══╝  ╚════██║");
        System.out.println("██║ ╚████║██████╔╝██║     ███████║");
        System.out.println("╚═╝  ╚═══╝╚═════╝ ╚═╝     ╚══════╝");
        System.out.println(COPY_RIGHT);
    }

    public static void pInfo(Object info) {
        System.out.println("[" + new Date().toString() + "] " + info.toString());
    }


    public static final String INVALID_HOST_NAME = "hostname 不合法";
    public static final String INVALID_PORT = "port 不合法";


    public static final String UNKNOWN_REQUEST_TYPE = "未知的请求";
    public static final String AUTHENTICATION_INCORRECT = "认证失败，认证秘钥不正确，青查看配置文件。";
}
