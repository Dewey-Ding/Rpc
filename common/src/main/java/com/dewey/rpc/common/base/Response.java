package com.dewey.rpc.common.base;

/**
 * 响应基类
 * @author dewey
 * @date 2018/9/8 23:31
 */
public class Response {
    private String requesId;

    private Exception exception;

    private Object result;

    public String getRequesId() {
        return requesId;
    }

    public void setRequesId(String requesId) {
        this.requesId = requesId;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
