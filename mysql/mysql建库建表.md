### 建库
CREATE DATABASE [dbName]

### 创建索引
```sql

# 删除索引
alter table easyrpt_export_where drop index unique_easyrpt_export_rpt_no_where;
# 创建索引
CREATE UNIQUE INDEX unique_easyrpt_export_rpt_no_where ON easyrpt_export_where(rpt_no,where_key,where_type);

# 查看沉余索引，sys系统库
schemal_r dundant_indexes


# 添加PRIMARY KEY(主键索引)
ALTER TABLE `table_name` ADD PRIMARY KEY ( `column` )
# 添加UNIQUE(唯一索引)
ALTER TABLE `table_name` ADD UNIQUE ( `column` )
# 添加INDEX(普通索引)
ALTER TABLE `table_name` ADD INDEX index_name ( `column` )
# 添加FULLTEXT(全文索引)
ALTER TABLE `table_name` ADD FULLTEXT ( `column`)
#添加多列索引
ALTER TABLE `table_name` ADD INDEX index_name ( `column1`, `column2`, `column3` )
```

### 事务
* 与 SQL 标准不同的地方在于InnoDB 存储引擎在 REPEATABLE-READ(可重读)事务隔离级别 下使用的是Next-Key Lock 锁算法，因此可以避免幻读的产生，这与其他数据库系统(如 SQL Server)是不同的。所以 说InnoDB 存储引擎的默认支持的隔离级别是 REPEATABLE-READ(可重读) 已经可以完全保证事务的隔离性要 求，即达到了 SQL标准的SERIALIZABLE(可串行化)隔离级别。

```

# 查看事务级别
# SERIALIZABLE 最高级别串行，完成符合ACID级别
# REPLACETABLE-READ 可重复读,存在幻读
# READ-COMMIT 读提交
# READ-UNCOMMITTED 读未提交， 最低级别，存在不可重复读、幻读、脏读
SELECT @@tx_isolation;
```
