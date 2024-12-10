package org.swu.apitemplate;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "统一响应对象", description = "返回给前端的通用响应格式")
public class ApiResult<T> {

    @ApiModelProperty(value = "响应状态码", example = "200", required = true)
    private int status;

    @ApiModelProperty(value = "响应消息", example = "操作成功", required = true)
    private String message;

    @ApiModelProperty(value = "响应数据", required = false)
    private T data;

    // 私有构造函数，防止直接实例化
    private ApiResult(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功的静态构造方法
     *
     * @param <T>  数据类型
     * @param data 返回的数据
     * @return ApiResult对象
     */
    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(200, "操作成功", data);
    }

    /**
     * 成功的静态构造方法（带自定义消息）
     *
     * @param <T>     数据类型
     * @param message 成功消息
     * @param data    返回的数据
     * @return ApiResult对象
     */
    public static <T> ApiResult<T> success(String message, T data) {
        return new ApiResult<>(200, message, data);
    }

    /**
     * 失败的静态构造方法
     *
     * @param status    错误码
     * @param message 错误消息
     * @return ApiResult对象
     */
    public static <T> ApiResult<T> error(int status, String message) {
        return new ApiResult<>(status, message, null);
    }

    /**
     * 通用的静态构造方法
     *
     * @param status   状态码
     * @param message 消息
     * @return ApiResult对象
     */
    public static <T> ApiResult<T> of(int status, String message) {
        return new ApiResult<>(status, message,null);
    }

    /**
     * 通用的静态构造方法
     *
     * @param status   状态码
     * @param message 消息
     * @param data    数据
     * @return ApiResult对象
     */
    public static <T> ApiResult<T> of(int status, String message, T data) {
        return new ApiResult<>(status, message, data);
    }

    // Getter 和 Setter 方法

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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
}

