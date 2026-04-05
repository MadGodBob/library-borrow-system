drop database if exists `librarys`;
create database `librarys`;
use librarys;

# 用户表
drop table if exists `user`;
create table `user` (
    `id` int(11) primary key not null auto_increment,
    `username` varchar(255) not null UNIQUE COMMENT '用户名',
    `password` varchar(255) not null comment '密码',
    `phone` varchar(255) comment '手机号',
    `email` varchar(255) comment '邮箱',
    `role` int(11) not null default 0 comment '权限，越高越大',
    `status` int(11) not null default 1 comment '状态，1正常，0禁用',
    `create_time` datetime default current_timestamp comment '创建时间'
) engine=InnoDB default charset=utf8 comment='用户表';

insert into `user` (`username`, `password`, `phone`, `email`, `role`, `status`) values
('admin', 'admin', '', '', 1, 1);

# 图书表
drop table if exists `book`;
create table `book` (
    `id` int(11) primary key not null auto_increment,
    `isbn` varchar(20) UNIQUE NOT NULL COMMENT 'ISBN',
    `title` varchar(200) not null comment '书名',
    `author` varchar(100) not null comment '作者',
    `publisher` varchar(100) comment '出版社',
    `publish_year` int COMMENT '出版年份',
    `category` varchar(100) comment '类别',
    `status` int(11) not null default 1 comment '状态',
    `create_time` datetime default current_timestamp comment '创建时间'
) engine=InnoDB default charset=utf8 comment='图书表';

# 借阅表
drop table if exists `borrow`;
create table `borrow` (
    `id` int(11) primary key not null auto_increment,
    `user_id` int(11) not null comment '借书人ID',
    `user_name` varchar(100) not null comment '借书人姓名',
    `book_id` int(11) not null comment '图书ID',
    `book_title` varchar(100) not null comment '图书标题',
    `borrow_time` datetime default current_timestamp comment '借阅时间',
    `return_time` datetime comment '归还时间',
    `status` int(11) not null default 1 comment '状态,1借阅中，0已归还'
) engine=InnoDB default charset=utf8 comment='借阅表';

# 操作日志表
drop table if exists `operation_log`;
create table `operation_log` (
    `id` int(11) primary key not null auto_increment,
    `user_id` int(11) not null comment '操作人ID',
    `user_name` varchar(100) not null comment '操作人姓名',
    `operation_type` int(11) not null comment '操作类型',
    `operation` varchar(255) not null comment '操作内容',
    `timestamp` datetime default current_timestamp comment '操作时间'
) engine=InnoDB default charset=utf8 comment='操作日志表';