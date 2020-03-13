# spring-jpa-mysql-smart-query

本项目为maven项目，下载源码，mvn install到本地仓库

## 原理：
1. 完全兼容jpa标准注解
2. 程序在启动过程中会检查@entity下的字段的注解的完整性，如果不全需要根据错误提示补全才能启动
3. 根据@Entity的表结构生成解析树，用于后续查询
4. 模仿url格式的查询语法，简单易懂
5. 自动生成json格式的数据返回，没有递归的危险

## 使用方法：
1. 在@SpringBootApplication的下面加上@EnableJpaSmartQuery
2. 在程序里使用SmartQuery的静态方法，例如：

```
SmartQuery.fetchList("user", "fields=*&username=wang&age[not]=12&score[lt]=100&score[gt]=90&page=0&size=1&sort=score,desc");
                     
```
## 支持5种静态方法：
```
long SmartyQuery.fetchCount(String entity, String queryString); //返回数量
```
```
HTTPListResponse SmartyQuery.fetchList(String entity, String queryString); //返回一个带分页的对象列表
```
```
HTTPListResponse SmartyQuery.fetchTree(String entity, String queryString); //返回一个多根树形的对象列表，对象entity必须有parent和children属性
```
```
HTTPListResponse SmartyQuery.fetchGroup(String entity, String queryString); //返回一个分组的列表，查询queryString里面必须包含group=***这种格式，指定分组字段
```
```
Map SmartyQuery.fetchOne(String entity, String queryString); //返回一个Map格式的对象
```

## 参数解释：
- entity为@Entity的类名，首字母小写

- queryString保留参数：fields ， page ， size ，sort，有特殊的意义
    - fields：返回的字段，可以为*，代表本entity所有的简单类型的属性；也可以是单独的属性或多个属性用逗号分隔；属性可以为简单属性或者对象，集合属性，也可以是两层属性 。fields=name,age,role.name。最好需要什么字段，指定什么字段，少使用*，提高mysql和网络性能
    - page：从0开始的页数。 page=1
    - size：每页的数量。 size=10
    - sort：按什么字段排序 ，例如：sort=age,desc ，可以是两层属性的排序，例如：sort=role.name,asc 
- queryString支持的符号：[gt],[gte],[lt],[lte],[in],[not],[like],[or],[eq]，用法见例子

- queryString支持两层复合条件查询。例如：fields=*&role.id=1&department.id[not]=2


## 配置参数
- 在application.yml或者properties中添加spring.jpa.mysql-smart-query.max-result-rows，默认值是5000，如需要可以改大，改小
- 本参数的意义是，数据库返回的原始数据的行数限制，left join或者  right join 返回的行数会比较大，要注意调节
