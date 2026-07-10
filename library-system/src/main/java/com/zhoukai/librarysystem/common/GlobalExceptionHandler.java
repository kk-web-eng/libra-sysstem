package com.zhoukai.librarysystem.common;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
/** 集中处理控制器抛出的异常，避免每个接口重复写 try/catch。 */
public class GlobalExceptionHandler {

    /**
     * 任何没有被单独处理的异常都会进入这里。
     * 返回统一 Result 后，前端响应拦截器会显示异常信息。
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        // 开发阶段打印完整调用栈，方便在控制台定位出错代码。
        e.printStackTrace();
        return Result.fail(e.getMessage());
    }
}
