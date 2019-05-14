# java-spring-boot-uid-generator-baidu
spring-boot 百度uid-generator 百度的唯一ID生成器



# 百度uid-generator

https://github.com/baidu/uid-generator

# 说明

升级spring boot 版本： 2.0.7.RELEASE

升级 mybatis，mybatis-spring 版本

升级 mysql-connector-java 版本：8.0.12

升级 junit 版本

# 案例

启动入口
```java
uid-provider 中的 ConsumerApplication
```


浏览器
```html
http://localhost:8080/uidGenerator
```

# 文档说明

https://blog.csdn.net/fenglailea/article/details/90200602


# 部分升级说明
>这里的升级，是升级 官方 代码依赖

官方代码地址：[https://github.com/baidu/uid-generator](https://github.com/baidu/uid-generator)

升级spring boot 版本： 2.0.7.RELEASE
升级 mybatis，mybatis-spring 版本
升级 mysql-connector-java 版本：8.0.12
升级 junit 版本
## 创建数据库存
导入官网数据库SQL [https://github.com/baidu/uid-generator/blob/master/src/main/scripts/WORKER_NODE.sql](https://github.com/baidu/uid-generator/blob/master/src/main/scripts/WORKER_NODE.sql)
也就是一张表
我这里是在`demo`库中，创建了这张表
```sql
DROP TABLE IF EXISTS WORKER_NODE;
CREATE TABLE WORKER_NODE
(
ID BIGINT NOT NULL AUTO_INCREMENT COMMENT 'auto increment id',
HOST_NAME VARCHAR(64) NOT NULL COMMENT 'host name',
PORT VARCHAR(64) NOT NULL COMMENT 'port',
TYPE INT NOT NULL COMMENT 'node type: ACTUAL or CONTAINER',
LAUNCH_DATE DATE NOT NULL COMMENT 'launch date',
MODIFIED TIMESTAMP NOT NULL COMMENT 'modified time',
CREATED TIMESTAMP NOT NULL COMMENT 'created time',
PRIMARY KEY(ID)
)
 COMMENT='DB WorkerID Assigner for UID Generator',ENGINE = INNODB;
```
如果报错，基本上是时间问题，因为mysql 低版本控制比较严格，解决方法有多种方式
方式一:
直接把`TIMESTAMP`改成`DATETIME` 即可
方式二:
执行SQL 语句前先执行:
```sql
set sql_mode="NO_ENGINE_SUBSTITUTION";
```
## mysql 配置信息更改
因为升级到8.x ,配置文件部分也要跟着修改
`uid-generator` 下，测试文件夹下的资源包`uid/mysql.properties` 
以下修改为
```java
mysql.driver=com.mysql.cj.jdbc.Driver
```
修改完成后，配置好数据库相关参数，这样单元测试即可执行成功
# 案例
计划将全局生成唯一ID作为一个服务提供者，供其他微服务使用调用
这里创建了一个项目，项目中包含两个子项目一个是`uid-generator`官方本身，当然你也可以不需要放到本项目中，直接使用官方的自行打包即可，一个是`uid-provider` 服务提供者

以下说明的主要是`服务提供者`
## 创建 子项目 uid-provider
如何创建 略
POM配置文件如下
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>java-spring-boot-uid-generator-baidu</artifactId>
        <groupId>com.foxwho.demo</groupId>
        <version>1.0-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>uid-provider</artifactId>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>1.3.2</version>
        </dependency>

        <!--for Mysql-->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <scope>runtime</scope>
            <version>8.0.12</version>
        </dependency>

        <!-- druid -->
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid-spring-boot-starter</artifactId>
            <version>1.1.16</version>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>${lombok.version}</version>
            <optional>true</optional>
        </dependency>

        <dependency>
            <groupId>com.foxwho.demo</groupId>
            <artifactId>uid-generator</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
    </dependencies>
</project>
```
## 复制 mapper 
先在`uid-provider`项目资源包路径下创建`mapper`文件夹，然后到官方`uid-generator`资源包路径下`META-INF/mybatis/mapper/WORKER_NODE.xml` 复制`WORKER_NODE.xml`文件，粘贴到该文件夹`mapper`内
## cache id 配置文件
先在`uid-provider`项目资源包路径下创建`uid`文件夹，然后到官方`uid-generator` `测试` [注意:`这里是测试资源包`] 资源包路径下`uid/cached-uid-spring.xml` 复制`cached-uid-spring.xml`文件，粘贴到该文件夹`uid`内

最后根据需要 配置参数，可以看官方说明
## 创建 spring boot 启动入口
主要就是加上注解`@MapperScan("com.baidu.fsg.uid")`让`mybatis`能扫描到`Mapper`类的包的路径

```java
package com.foxwho.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;


@SpringBootApplication
@MapperScan("com.baidu.fsg.uid")
public class ConsumerApplication {


    public static void main(String[] args) {
        new SpringApplicationBuilder(ConsumerApplication.class).run(args);
    }
}

```
## 创建配置
```java
package com.foxwho.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource(locations = { "classpath:uid/cached-uid-spring.xml" })
public class UidConfig {
}

```
## 创建服务接口
```java
package com.foxwho.demo.service;

import com.baidu.fsg.uid.UidGenerator;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
public class UidGenService {
    @Resource(name = "cachedUidGenerator")
    private UidGenerator uidGenerator;

    public long getUid() {
        return uidGenerator.getUID();
    }
}
```
主要说明一下`@Resource(name = "cachedUidGenerator")` 以往错误都是少了这里，`没有`标明`注入来源`
## 控制器
```java
package com.foxwho.demo.controller;

import com.foxwho.demo.service.UidGenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UidController {
    @Autowired
    private UidGenService uidGenService;

    @GetMapping("/uidGenerator")
    public String UidGenerator() {
        return String.valueOf(uidGenService.getUid());
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }
}

```
## 项目配置文件
```java
server.port=8080

spring.datasource.url=jdbc:mysql://127.0.0.1:3306/demo?useUnicode=true&characterEncoding=utf-8&useSSL=false
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

mybatis.mapper-locations=classpath:mapper/*.xml
mybatis.configuration.map-underscore-to-camel-case=true
```

## 启动项目
从启动入口，启动
访问浏览器
```HTML
http://localhost:8080/uidGenerator
```
页面输出
```HTML
13128615512260612
```