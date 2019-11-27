package cn.shazhengbo.utils

import cn.shazhengbo.entity.ColumnEntity
import cn.shazhengbo.entity.TableEntity
import org.apache.commons.configuration.Configuration
import org.apache.commons.configuration.PropertiesConfiguration
import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.WordUtils
import org.apache.velocity.Template
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.VelocityEngine
import org.codehaus.groovy.control.ConfigurationException

/**
 * @author:CrazyShaQiuShi
 * @email:3105334046@qq.com
 * @descript:
 * @version:1.0.0
 */
class GenUtils {


    static List<String> getTemplates() {
        List<String> templates = new ArrayList<String>();
        templates.add("templates/mybatis/Entity.java.vm");
        templates.add("templates/mybatis/Mapper.java.vm");
        templates.add("templates/mybatis/Mapper.xml.vm");
        templates.add("templates/mybatis/Service.java.vm");
        templates.add("templates/mybatis/ServiceImpl.java.vm");
        templates.add("templates/mybatis/Controller.java.vm");
        templates.add("templates/dto/QueryInput.java.vm");
        templates.add("templates/dto/EntityInput.java.vm");
        return templates;
    }

    /**
     * 获取配置信息
     */
    static Configuration getConfig() {
        try {
            new PropertiesConfiguration("generator.properties")
        } catch (ConfigurationException e) {
            throw new Exception("获取配置文件失败，", e);
        }
    }
    /**
     * 逆向生成代码
     * @param table
     * @param columns
     * @param config 配置信息
     */
    static void generatorCode(Map<String, String> table, List<Map<String, String>> columns, Configuration config, List<String> templates) {
        boolean hasBigDecimal = false;
        //表信息
        TableEntity tableEntity = new TableEntity(tableName: table.get("tableName"), comments: table.get("tableComment"))
        //表名转换成Java类名
        String className = tableToJava(tableEntity.tableName, config.getString("tablePrefix"))
        tableEntity.setClassName(className)
        tableEntity.setClassname(StringUtils.uncapitalize(className))
        /**
         * 列信息
         */
        List<ColumnEntity> columsList = new ArrayList<>();
        for (Map<String, String> column : columns) {
            ColumnEntity columnEntity = new ColumnEntity(
                    columnName: column.get("columnName"),
                    dataType: column.get("dataType"),
                    comments: column.get("columnComment"),
                    extra: column.get("extra"))
            /**
             * 列名转换成Java属性名
             */
            String attrName = columnToJava(columnEntity.getColumnName())
            columnEntity.setAttrName(attrName)
            columnEntity.setAttrname(StringUtils.uncapitalize(attrName));
            /**
             * 列的数据类型，转换成Java类型
             */
            String attrType = config.getString(columnEntity.getDataType(), "unknowType");
            columnEntity.setAttrType(attrType);
            if (!hasBigDecimal && attrType.equals("BigDecimal")) {
                hasBigDecimal = true;
            }
            /**
             * 是否主键
             */
            if ("PRI".equalsIgnoreCase(column.get("columnKey")) && tableEntity.getPk() == null) {
                tableEntity.setPk(columnEntity);
            }
            columsList.add(columnEntity);
        }
        tableEntity.setColumns(columsList);
        /**
         * 没主键，则第一个字段为主键
         */
        if (tableEntity.getPk() == null) {
            tableEntity.setPk(tableEntity.getColumns().get(0));
        }
        /**
         * 获取初始化模板引擎
         */
        VelocityEngine velocityEngine = new VelocityEngine();

        /**
         * 设置velocity资源加载器
         */
        Properties prop = new Properties();
        prop.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        prop.put("velocimacro.library", "");
        velocityEngine.init(prop);
        String mainPath = config.getString("mainPath");
        mainPath = StringUtils.isBlank(mainPath) ? "com.yqtsoft" : mainPath;
        /**
         * 封装模板数据
         */
        Map<String, Object> map = new HashMap<>();
        map.put("tableName", tableEntity.getTableName());
        map.put("comments", tableEntity.getComments());
        map.put("pk", tableEntity.getPk());
        map.put("className", tableEntity.getClassName());
        map.put("classname", tableEntity.getClassname());
        map.put("pathName", tableEntity.className.toLowerCase());
        map.put("columns", tableEntity.getColumns());
        map.put("hasBigDecimal", hasBigDecimal);
        map.put("mainPath", mainPath);
        map.put("package", config.getString("package"));
        map.put("moduleName", config.getString("moduleName"));
        map.put("author", config.getString("author"));
        map.put("email", config.getString("email"));
        map.put("datetime", DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN))
        map.put("version", "version")
        map.put("createdBy", "createdBy")
        map.put("createdTime", "createdTime")
        map.put("lastModifiedBy", "lastModifiedBy")
        map.put("lastModifiedTime", "lastModifiedTime")
        VelocityContext context = new VelocityContext(map);
        def disk = config.getString("disk")
        /**
         * 解析渲染模板
         */
        for (template in templates) {
            /**
             * 渲染模板
             */
            StringWriter sw = new StringWriter();
            Template tpl = velocityEngine.getTemplate(template, "UTF-8");
            tpl.merge(context, sw);
            try {
                String dirPath = getFileDir(template, config.getString("package"), config.getString("moduleName"))
                print(dirPath)
                String fileName = getFileName(template, tableEntity.getClassName())

                /**
                 * 添加输出信息
                 */
                writerFile(disk + File.separator + dirPath, fileName, sw.toString())
            } catch (IOException e) {
                throw new Exception("渲染模板失败，表名：" + tableEntity.getTableName(), e);
            }
        }
    }
    /**
     *  根据查询出的表列表和单元信息
     */
    static void generatorCode(Map<String, String> table,
                              List<Map<String, String>> columns) {
        /**
         * 配置信息
         */
        Configuration config = getConfig();

        /**
         * 默认模板列表
         */
        List<String> templates = getTemplates();
        generatorCode(table, columns, config, templates);
    }
    /**
     * 使用默认模板
     * @param table 表列表
     * @param columns 列信息
     * @param configuration 配置信息
     */
    static void generatorCode(Map<String, String> table,
                              List<Map<String, String>> columns, Configuration configuration) {
        /**
         * 默认模板列表
         */
        List<String> templates = getTemplates();
        generatorCode(table, columns, configuration, templates);
    }

    /**
     * 写文件
     * @param path
     * @param fileName
     * @param context
     * @return
     */
    static writerFile(String path, String fileName, String context) {
        def dir = new File(path)
        if (dir.exists()) {
            dir.delete()
        }
        dir.mkdirs();
        def file = new File(path + fileName)
        if (file.exists()) {
            file.delete()
        }
        def printWriter = file.newPrintWriter();
        printWriter.println(context)
        printWriter.flush()
        printWriter.close()
    }

    /**
     * 列名转换成Java属性名
     * @param columnName
     * @return
     */
    static String columnToJava(String columnName) {
        return WordUtils.capitalizeFully(columnName, "_".toCharArray()).replace("_", "");
    }

    /**
     * 表名转换成Java类名
     */
    static String tableToJava(String tableName, String tablePrefix) {
        if (StringUtils.isNotBlank(tablePrefix)) {
            tableName = tableName.replace(tablePrefix, "");
        }
        println(tableName)
        return columnToJava(tableName);
    }

    /**
     * 获取文件名
     */
    static String getFileName(String template, String className) {
        if (template.contains("Entity.java.vm")) {
            return className + ".java";
        }

        if (template.contains("Mapper.java.vm")) {
            return "I" + className + "Mapper.java";
        }

        if (template.contains("Service.java.vm")) {
            return "I" + className + "Service.java";
        }

        if (template.contains("ServiceImpl.java.vm")) {
            return className + "ServiceImpl.java";
        }

        if (template.contains("Controller.java.vm")) {
            return className + "Controller.java";
        }

        if (template.contains("Mapper.xml.vm")) {
            return className + "Dao.xml";
        }
        if (template.contains("list.html.vm")) {
            return className.toLowerCase() + ".html";
        }
        if (template.contains("list.js.vm")) {
            return className.toLowerCase() + ".js";
        }
        if (template.contains("QueryInput.java.vm")) {
            return className + "QueryInput.java";
        }
        if (template.contains("EntityInput.java.vm")) {
            return className + "Input.java";
        }
        return null;
    }

    /**
     * 获取文件目录
     */
    static String getFileDir(String template, String packageName, String moduleName) {
        String packagePath = "main" + File.separator + "java" + File.separator;
        if (StringUtils.isNotBlank(packageName)) {
            packagePath += packageName.replace(".", File.separator) + File.separator + moduleName + File.separator;
        }

        if (template.contains("Entity.java.vm")) {
            return packagePath + "entity" + File.separator
        }

        if (template.contains("Mapper.java.vm")) {
            return packagePath + "mapper" + File.separator
        }

        if (template.contains("Service.java.vm")) {
            return packagePath + "service" + File.separator
        }

        if (template.contains("ServiceImpl.java.vm")) {
            return packagePath + "service" + File.separator + "impl" + File.separator
        }

        if (template.contains("Controller.java.vm")) {
            return packagePath + "controller" + File.separator
        }

        if (template.contains("Mapper.xml.vm")) {
            return "main" + File.separator + "resources" + File.separator + "mapper" + File.separator + moduleName + File.separator
        }
        if (template.contains("list.html.vm")) {
            return "main" + File.separator + "resources" + File.separator + "views" + File.separator + "modules" + File.separator + moduleName + File.separator
        }

        if (template.contains("list.js.vm")) {
            return "main" + File.separator + "resources" + File.separator + "views" + File.separator + "modules" + File.separator + moduleName + File.separator + "js" + File.separator
        }
        if (template.contains("QueryInput.java.vm")) {
            return packagePath + "dto" + File.separator + "input" + File.separator
        }
        if (template.contains("EntityInput.java.vm")) {
            return packagePath + "dto" + File.separator + "input" + File.separator
        }
        return null;
    }

}