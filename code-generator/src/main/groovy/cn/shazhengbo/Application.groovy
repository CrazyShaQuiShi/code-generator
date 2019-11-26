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
