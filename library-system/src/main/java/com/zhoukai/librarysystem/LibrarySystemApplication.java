package com.zhoukai.librarysystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LibrarySystemApplication {

    /**
 * 后端程序的启动入口。
     * SpringApplication.run 会创建 Spring 容器、扫描组件，并启动内置 Web 服务器。
     */
    public static void main(String[] args) {
        SpringApplication.run(LibrarySystemApplication.class, args);
    }

}
