# AutoCache
解决缓存问题

#java编译参数
javac -parameters

#问题
1.  单表缓存更新
    1.  相同字段缓存更新: ID更新数据库, 更新ID的缓存
    2.  跨字段缓存更新: 使用ID更新了数据库, 但是需要更新手机号的缓存
2.  关联表缓存更新: 两张表关联查询获得的结果
    1.  某一张表数据更新后缓存刷新: 在表key列表中存储关联的所有表的ID作为KEY
3.  接口无参缓存


#使用

#针对数据库对象
继承com.vanxd.autocache.core.entity.BaseEntity