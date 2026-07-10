package com.zhoukai.librarysystem.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * 统一后端接口的返回格式。
 * 前端只需要判断 code，就能知道请求成功、未登录、无权限或业务失败。
 */
public class Result<T> {
    // 状态码：200 成功，401 未登录，403 无权限，500 一般业务错误。
    private int code;
    // 提示文字，前端可直接显示给用户。
    private String msg;
    // 真正的业务数据；没有数据时为 null。
    private T data;

    // 返回“成功 + 数据”。
    public static <T> Result<T> ok(T data) {
        return new Result<>(200, "操作成功", data);
    }

    // 返回“成功但没有额外数据”。
    public static <T> Result<T> ok() {
        return new Result<>(200, "操作成功", null);
    }

    // 返回默认的业务失败状态。
    public static <T> Result<T> fail(String msg) {
        return new Result<>(500, msg, null);
    }

    // 返回指定状态码，权限控制会使用 401 或 403。
    public static <T> Result<T> fail(int code, String msg) {
        return new Result<>(code, msg, null);
    }
}
