package com.zx.workflow.comon;

import lombok.Data;

@Data
public class ResultBean<T>{
    //响应码
    private int code;
    private String msg;
    private T data;

    public ResultBean(){}

    public ResultBean(int code) {
        this.code = code;
    }

    public ResultBean(String msg) {
        this.msg = msg;
    }

    public ResultBean(T data){
        this.data = data;
    }

    public ResultBean(int code,  String msg,T data){
        this.code = code;
        this.data = data;
        this.msg = msg;
    }

    public ResultBean(int code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public ResultBean(int code, T data){
        this.code = code;
        this.data = data;
    }

}
