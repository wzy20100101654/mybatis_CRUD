# mybatis_CRUD
MyBatis框架之对数据库的增删改查操作CRUD实践及细节点总结

目录

1 保存操作

2 修改操作

3 删除操作

4 查询操作

本博文总结下使用MyBatis的CRUD操作，关于MyBatis的环境搭建就不再赘述了，可以回顾下以前的博文，本次也是在这个工厂的基础上进行的。

1 保存操作
需求：保存一个用户对象，并获取保存数据的id值。

1）UserDao接口中定义好保存操作方法

//用户持久层接口
public interface UserDao {
    //保存用户
    void saveUser(User user);
}
2）映射配置文件中增加配置

<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="com.winter.dao.UserDao">
<!--    保存用户-->
    <insert id="saveUser" parameterType="com.winter.domain.User">
--         配置插入后获取插入数据的id
        <selectKey keyProperty="id" keyColumn="id" resultType="Integer" order="AFTER">
            select last_insert_id();
        </selectKey>
        insert into user(username,address,sex,birthday) values(#{username},#{address},#{sex},#{birthday});
    </insert>
</mapper>

【注意】：

parameterType属性，代表的是参数的类型，我们写要传入类的全名称；
sql中用#{}表示占位符，类似于前面学习JDBC中的?，用来替换实际的数据；
3）添加测试类中的保存测试方法

【注意】：

获取dao代理对象和释放资源封装在了两个方法中，并通过@before和@after注解执行；
在释放资源方法中，释放资源前要进行commit事务提交，因为其默认是手动提交的，否则执行保存用户方法后不会插入数据；
关于执行testSave方法后插入了两条数据，怀疑是使用maven构建，此处又使用@Test运行，可能是运行了两次导致实际插入两次数据，在File-Settings-Maven-Runner 中勾选skip Tests即可解决。
//测试MyBatis的CRUD
public class MyBatisTest {
    private InputStream in;
    private SqlSession sqlSession;
    private UserDao userDao;

    @Before  //在测试方法执行前执行
    public void init() throws Exception{
        //1、读取配置文件，生成字节输入流
       in = Resources.getResourceAsStream("SqlMapConfig.xml");
        //2、获取SqlSessionFactory
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(in);
        //3、获取SqlSession对象
        sqlSession = factory.openSession();
        //4、获取dao的代理对象
        userDao = sqlSession.getMapper(UserDao.class);
    }
    @After //测试方法执行后执行
    public void destroy() throws Exception{
        //提交事务
        sqlSession.commit();
        //6、释放资源
        sqlSession.close();
        in.close();
    }
    //测试保存
    @Test
    public void testSave(){
        User user = new User();
        user.setUsername("MyBatis new insert");
        user.setAddress("BJ");
        user.setSex("男");
        user.setBirthday(new Date());
        System.out.println("保存前："+user);
        //5.执行保存方法
        userDao.saveUser(user);
        System.out.println("保存后："+user);
    }
}



2 修改操作
1）UserDao接口中定义好修改操作方法

//用户持久层接口
public interface UserDao {
    //修改用户
    void updateUser(User user);
}

2）映射配置文件中增加配置

<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="com.winter.dao.UserDao">
<!--    修改用户-->
    <update id="updateUser" parameterType="com.winter.domain.User">
        update user set username=#{username},address=#{address},sex=#{sex},birthday=#{birthday} where id=#{id};
    </update>
</mapper>

3）添加测试类中的保存测试方法

    //测试修改
    @Test
    public void testUpdate(){
        User user = new User();
        user.setId(25);
        user.setUsername("MyBatis_Update");
        user.setAddress("DQ");
        user.setSex("女");
        user.setBirthday(new Date());
        //5.执行修改方法
        userDao.updateUser(user);
    }



3 删除操作
1）UserDao接口中定义好删除操作方法

//用户持久层接口
public interface UserDao {
    //删除用户,根据ID
    void deleteUser(Integer userId);
}

2）映射配置文件中增加配置

<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="com.winter.dao.UserDao">
<!--    删除用户-->
    <delete id="deleteUser" parameterType="Integer">
        delete from user where id = #{userId}
    </delete>
</mapper>

3）添加测试类中的删除测试方法

    //测试删除
    @Test
    public void testDelete(){
        //5.执行删除方法
        userDao.deleteUser(24);
    }



4 查询操作
这里实现两个功能，一个根据id查询用户，另一个是根据名字模糊查询。

1）UserDao接口中定义好查询操作方法
//用户持久层接口
public interface UserDao {
    //根据id查询用户
    User findById(Integer userId);
    //模糊查询
    List<User> findByName(String username);
}

2）映射配置文件中增加配置

<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="com.winter.dao.UserDao">
<!--    根据id查询用户-->
    <select id="findById" parameterType="Integer" resultType="com.winter.domain.User">
        select *from user where id=#{userId};
    </select>
<!--    根据名称模糊查询-->
    <select id="findByName" parameterType="String" resultType="com.winter.domain.User">
        select *from user where username like #{username}
    </select>
</mapper>

3）添加测试类中的查询测试方法

//测试查询一个用户
    @Test
    public void testFindById(){
        //5.执行查询
        User user = userDao.findById(25);
        System.out.println(user);//User{id=25, username='MyBatis_Update', address='DQ', sex='女', birthday=Mon Sep 14 17:01:09 CST 2020}
    }

    //测试模糊查询
    @Test
    public void testFindByName(){
        //5.执行查询
        List<User> users = userDao.findByName("%王%");
        for (User user : users) {
            System.out.println(user);
        }
    }



【注意】：模糊查询还有另外一种配置方式，对应的测试类中的方法参数也要变更，去掉%

    <select id="findByName" parameterType="String" resultType="com.winter.domain.User">
    <!--select *from user where username like #{username}-->
    select *from user where username like '%${value}%'
    </select>

        //5.执行查询
//        List<User> users = userDao.findByName("%王%");
        List<User> users = userDao.findByName("王");

这种配置方式，value名字是固定的，不能改为其他的，且这种相对于前一种，虽然效果一样，但是执行的sql语句不同：





由此，我们可以发现，#{}用的是PreparedStatement的参数占位符，可以有效防止sql注入问题，而${}用的是Statement对象的字符串拼接SQL。实际开发中我们使用#{}比较靠谱。

需要源码的传送门在此。

———————————————————————————————————

本文为博主原创文章，转载请注明出处！

若本文对您有帮助，轻抬您发财的小手，关注/评论/点赞/收藏，就是对我最大的支持！

祝君升职加薪，鹏程万里！
