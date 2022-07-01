# project-common-es基于RestHighLevelClient 封装

## 功能介绍
1. 代理装配。无需书写实现。基层是RestHighLevelClient
2. 支持dsl语句与sql语句查询
3. 自定义返回值类型，支持Page、list、set、array等自定义对象

## 玩法说明
1.书写es对应实体类，添加注解@EsMetaData 、声明索引名称
@EsMetaData(indexName = "es_test_index")
public class EsTestDemo extends EsBaseEntity {
private String name;
private Integer age;
}
2.书写实体对应的dao extends BaseEsRepository<EsTestDemo> 指定对应的泛型
public interface TestEsDao extends BaseEsRepository<EsTestDemo> {
}
里面有些基础方法
3.自定义方法，支持sql查询与dsl语句查询 $就是参数的替换 #则会对参数进行处理，如字符串加'',日期格式化等
@EsSelect("SELECT * FROM es_test_index WHERE name like '${es.name}%' and age = #{age} limit ${(pageNum-1)*pageSize},#{pageSize}")
Page<EsTestDemo> searchLikePage(@EsParam("es") EsTestDemo esTestDemo,@EsParam("age")Integer age,@EsParam("pageNum") Integer pageNum,@EsParam("pageSize")Integer pageSize);
}


