package com.iot.nero.nraft.client;


import com.iot.nero.nraft.entity.response.Response;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/7/4
 * Time   12:10 PM
 */
public interface RpcErrorListener {
    void onError(Response<Object> response);

    void onConnectionException(Exception e);
}
