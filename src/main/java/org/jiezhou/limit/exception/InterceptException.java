package org.jiezhou.limit.exception;

public class InterceptException extends RuntimeException{

    private String msg;

    public InterceptException(String msg){
        this.msg = msg;
    }

    public InterceptException(){}

    public String getMsg(){
        return msg;
    }
}
