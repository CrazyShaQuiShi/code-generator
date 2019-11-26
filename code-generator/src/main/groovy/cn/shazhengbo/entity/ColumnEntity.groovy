package cn.shazhengbo.entity

/**
 * @author:CrazyShaQiuShi
 * @email:3105334046 @ q q . c o m
 * @descript:
 * @version:1.0.0
 */
class ColumnEntity {
    /**
     * 列名
     */
    def  columnName
    /**
     * 列名类型
     */
    def  dataType
    /**
     * 列名备注
     */
    def  comments
    /**
     * 属性名称(第一个字母大写)，如：user_name => UserName
     */
    def  attrName
    /**
     * 属性名称(第一个字母小写)，如：user_name => userName
     */
    def  attrname
    /**
     * 属性类型
     */
    def  attrType
    /**
     * auto_increment
     */
    def  extra
}
