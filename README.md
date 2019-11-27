# code-generator

#### 介绍
代码生成器

#### 软件架构
快速生成代码模板，提升开发效率

#### 安装教程

```xml
 <dependency>
 	<groupId>cn.shazhengbo</groupId>
	<artifactId>code-generator</artifactId>
 	<version>1.0.2</version>
 </dependency>
```


**配置文件**

generator.properties

```properties
#代码生成器，配置信息
mainPath=com.yqtsoft
#包名
package=com.yqtsoft.modules
moduleName=project
#作者
author=CrazyShaQiuShi
#Email
email=3105334046@qq.com
#表前缀(类名不会包含表前缀)
tablePrefix=
#类型转换，配置信息
tinyint=Integer
smallint=Integer
mediumint=Integer
int=Integer
integer=Integer
bigint=Long
float=Float
double=Double
decimal=BigDecimal
bit=Boolean
char=String
varchar=String
tinytext=String
text=String
mediumtext=String
longtext=String
date=Date
datetime=Date
timestamp=Date
#这里可指定到项目src目录下
disk=D:
```

**配置数据源连接**

```groovy
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <environments default="development">
        <environment id="development">
            <transactionManager type="JDBC"/>
            <dataSource type="POOLED">
                <property name="driver" value="com.mysql.jdbc.Driver"/>
                <property name="url" value="jdbc:mysql://192.168.0.39:3306/dn"/>
                <property name="username" value="root"/>
                <property name="password" value="cdf1234~"/>
            </dataSource>
        </environment>
    </environments>
    <mappers>
        <mapper resource="mapper/tablemapper.xml"/>
    </mappers>
</configuration>
```


#### 使用默认的配置和数据源

```groovy
package cn.shazhengbo

import cn.shazhengbo.mapper.TableMapper
import cn.shazhengbo.utils.GenUtils
import org.apache.ibatis.io.Resources
import org.apache.ibatis.session.SqlSession
import org.apache.ibatis.session.SqlSessionFactory
import org.apache.ibatis.session.SqlSessionFactoryBuilder

class Application {

    static void main(String[] args) {
        String resource = "mybatis-configuartion.xml"
        InputStream inputStream = Resources.getResourceAsStream(resource)
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream)
        SqlSession session = sqlSessionFactory.openSession()
        def tableMapper = session.getMapper(TableMapper.class)

        /**
         * 需要自动生成代码的表名
         */
        def tableNames = ['project_progress'] as String[]
        /**
         * 生成代码
         */
        generatorCode(tableMapper, tableNames)

    }

    static def generatorCode(TableMapper tableMapper, String[] tableNames) {
        for (String tableName : tableNames) {
            /**
             * 查询表信息
             */
            Map<String, String> table = tableMapper.queryTable(tableName)
            /**
             * 查询列信息
             */
            List<Map<String, String>> columns = tableMapper.queryColumns(tableName)
            /**
             * 生成代码
             */
            GenUtils.generatorCode(table, columns)
        }
    }
}

```


##### 使用自定义数据源和配置文件

```java
package com.test;

import cn.shazhengbo.mapper.TableMapper;
import cn.shazhengbo.utils.GenUtils;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class Demo {
    public static void main(String[] args) throws IOException, ConfigurationException {
        String resource = "mybatis-configuartion.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession session = sqlSessionFactory.openSession();
        TableMapper tableMapper = session.getMapper(TableMapper.class);

        /**
         * 需要自动生成代码的表名
         */
        String[] tableNames = new String[]{"project_progress"};
        /**
         * 生成代码
         */
        generatorCode(tableMapper, tableNames,new PropertiesConfiguration("自定义文件.properties"));
    }

    static void generatorCode(TableMapper tableMapper, String[] tableNames, Configuration configuration) {
        for (String tableName : tableNames) {
            /**
             * 查询表信息
             */
            Map<String, String> table = tableMapper.queryTable(tableName);
            /**
             * 查询列信息
             */
            List<Map<String, String>> columns = tableMapper.queryColumns(tableName);

            /**
             * 生成代码
             */
           GenUtils.generatorCode(table,columns,configuration);
        }
    }
}
```