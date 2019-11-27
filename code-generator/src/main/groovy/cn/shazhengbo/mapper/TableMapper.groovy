package cn.shazhengbo.mapper
/**
 * @author:CrazyShaQiuShi
 * @email:3105334046 @ q q . c o m
 * @descript:
 * @version:1.0.0
 */
interface TableMapper {
    /**
     * 查询表详情信息
     * @param tableName
     * @return
     */
    Map<String, String> queryTable(String tableName);

    /**
     *  查询表结构
     * @param tableName
     * @return
     */
    List<Map<String, String>> queryColumns(String tableName);
}
