CREATE DATABASE IF NOT EXISTS library_training
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE library_training;

DROP TABLE IF EXISTS borrow_record;
DROP TABLE IF EXISTS book_info;
DROP TABLE IF EXISTS reader_info;
DROP TABLE IF EXISTS sys_user;

CREATE TABLE sys_user (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    username    VARCHAR(50)  NOT NULL COMMENT '登录账号',
    password    VARCHAR(100) NOT NULL COMMENT '登录密码',
    real_name   VARCHAR(50)  NOT NULL COMMENT '真实姓名',
    role        VARCHAR(20)  NOT NULL DEFAULT 'ADMIN' COMMENT '角色',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '1启用,0禁用',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户表';

CREATE TABLE reader_info (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '读者ID',
    reader_no   VARCHAR(30)  NOT NULL COMMENT '读者编号/学号',
    password    VARCHAR(100) DEFAULT NULL COMMENT '登录密码',
    name        VARCHAR(50)  NOT NULL COMMENT '读者姓名',
    gender      VARCHAR(10)  DEFAULT NULL COMMENT '性别',
    phone       VARCHAR(20)  DEFAULT NULL COMMENT '联系电话',
    class_name  VARCHAR(50)  DEFAULT NULL COMMENT '班级/部门',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '1正常,0停用',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_reader_no (reader_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='读者信息表';

CREATE TABLE book_info (
    id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '图书ID',
    isbn            VARCHAR(30)  NOT NULL COMMENT 'ISBN/图书编号',
    book_name       VARCHAR(100) NOT NULL COMMENT '书名',
    author          VARCHAR(50)  DEFAULT NULL COMMENT '作者',
    publisher       VARCHAR(100) DEFAULT NULL COMMENT '出版社',
    category        VARCHAR(50)  DEFAULT NULL COMMENT '图书分类',
    description     VARCHAR(500) DEFAULT NULL COMMENT '图书简介',
    total_count     INT          NOT NULL DEFAULT 1 COMMENT '馆藏总数量',
    available_count INT          NOT NULL DEFAULT 1 COMMENT '可借数量',
    location        VARCHAR(50)  DEFAULT NULL COMMENT '书架位置',
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_isbn (isbn)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='图书信息表';

CREATE TABLE borrow_record (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '借阅记录ID',
    book_id     BIGINT       NOT NULL COMMENT '关联图书ID',
    reader_id   BIGINT       NOT NULL COMMENT '关联读者ID',
    reader_name VARCHAR(50)  DEFAULT NULL COMMENT '读者姓名',
    reader_phone VARCHAR(20) DEFAULT NULL COMMENT '读者电话',
    book_isbn   VARCHAR(30)  DEFAULT NULL COMMENT '书籍编号',
    book_name   VARCHAR(100) DEFAULT NULL COMMENT '书籍名称',
    borrow_date DATE         NOT NULL COMMENT '借出日期',
    due_date    DATE         NOT NULL COMMENT '应还日期',
    return_date DATE         DEFAULT NULL COMMENT '实际归还日期',
    status      TINYINT      NOT NULL DEFAULT 0 COMMENT '0借阅中,1已归还,2逾期',
    operator_id BIGINT       DEFAULT NULL COMMENT '操作员ID',
    remark      VARCHAR(255) DEFAULT NULL COMMENT '备注',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_book_id (book_id),
    KEY idx_reader_id (reader_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='借阅记录表';

TRUNCATE TABLE sys_user;
INSERT INTO sys_user (username, password, real_name, role, status)
VALUES ('admin', '123456', '系统管理员', 'SUPER_ADMIN', 1);

INSERT INTO reader_info (reader_no, name, gender, phone, class_name, status) VALUES
('R2026001', '张明', '男', '13800000001', '软件工程一班', 1),
('R2026002', '李雪', '女', '13800000002', '数字媒体二班', 1),
('R2026003', '王晨', '男', '13800000003', '教师办公室', 1);

INSERT INTO book_info (isbn, book_name, author, publisher, category, total_count, available_count, location) VALUES
('9787115546081', 'Java 核心技术', 'Cay S. Horstmann', '机械工业出版社', '计算机', 5, 5, 'A-01-01'),
('9787115428028', 'Vue.js 实战', '梁灏', '人民邮电出版社', '前端开发', 4, 4, 'A-02-03'),
('9787302517597', '数据库系统概论', '王珊', '高等教育出版社', '数据库', 6, 6, 'B-03-02'),
('9787111213826', '算法导论', 'Thomas H. Cormen', '机械工业出版社', '算法', 3, 3, 'C-01-04');
