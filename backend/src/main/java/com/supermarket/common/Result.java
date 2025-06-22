package com.supermarket.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * 通用响应结果类
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
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

    public Result(boolean success, String message, T data, Integer code) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.code = code;
    }

    // 成功响应（无数据）
    public static <T> Result<T> success() {
        return new Result<>(true, "操作成功", null, 200);
    }

    // 成功响应（带数据）
    public static <T> Result<T> success(T data) {
        return new Result<>(true, "操作成功", data, 200);
    }

    // 成功响应（带消息）
    public static <T> Result<T> success(String message) {
        return new Result<>(true, message, null);
    }

    // 成功响应（带数据和消息）
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(true, message, data, 200);
    }

    // 失败响应
    public static <T> Result<T> error(String message) {
        return new Result<>(false, message, null, 500);
    }

    // 失败响应（带错误码）
    public static <T> Result<T> error(String message, Integer code) {
        return new Result<>(false, message, null, code);
    }
}