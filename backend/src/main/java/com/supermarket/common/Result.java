package com.supermarket.common;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 通用响应结果类
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> {
    private boolean success;
    private String message;
    private T data;
    private Integer code;

    // 私有构造方法
    private Result() {}

    public Result(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.code = success ? 200 : 500;
    }

    // 成功响应（无数据）
    public static <T> Result<T> success() {
        return new Result<>(true, null, null);
    }

    // 成功响应（带数据）
    public static <T> Result<T> success(T data) {
        return new Result<>(true, null, data);
    }

    // 成功响应（带消息）
    public static <T> Result<T> success(String message) {
        return new Result<>(true, message, null);
    }

    // 成功响应（带数据和消息）
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(true, message, data);
    }

    // 失败响应
    public static <T> Result<T> error(String message) {
        return new Result<>(false, message, null);
    }

    // 失败响应（带错误码）
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>(false, message, null);
        result.setCode(code);
        return result;
    }

    // Getter和Setter方法
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "Result{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", code=" + code +
                '}';
    }
}