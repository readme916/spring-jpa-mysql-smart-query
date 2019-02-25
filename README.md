# spring-jpa-mysql-smart-query

本项目为maven项目，下载源码，mvn install到本地仓库

使用方法：

在@SpringBootApplication的下面加上@EnableJpaSmartQuery

使用静态方法 SmartQuery.fetchList("user", "fields=*&username=wang&age[not]=12&score[lt]=100&score[gt]=90&page=0&size=1&sort=score,desc");

支持方法：fetchCount ，fetchList，fetchTree，fetchGroup，fetchOne
支持的符号：[gt],[gte],[lt],[lte],[in],[not],[like],[or],[eq]

支持符合属性的查询：
例如 fields=roleObject,departmentObject,*&roleObject.id=1&departmentObject.id[not]=2
