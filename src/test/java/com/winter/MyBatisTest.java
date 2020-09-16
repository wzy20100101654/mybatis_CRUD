package com.winter;

import com.winter.dao.UserDao;
import com.winter.domain.QueryVo;
import com.winter.domain.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.security.spec.ECField;
import java.util.Date;
import java.util.List;

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
    //测试查询所有
    @Test
    public void testFindAll() {

        //5、执行查询所有方法
        List<User> users = userDao.findAll();
        for (User user : users) {
            System.out.println(user);
        }
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

    //测试删除
    @Test
    public void testDelete(){
        //5.执行删除方法
        userDao.deleteUser(24);
    }

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
//        List<User> users = userDao.findByName("王");
        for (User user : users) {
            System.out.println(user);
        }
    }

    //测试查询总用户记录数
    @Test
    public void testFindTotal(){
        //5.执行查询
        int total = userDao.findTotal();
        System.out.println(total);
    }

    //测试使用QueryVo作为查询条件
    @Test
    public void testFindUserByVo(){
        QueryVo vo = new QueryVo();
        User user = new User();
        user.setUsername("%王%");
        vo.setUser(user);
        //5.执行查询
        List<User> users = userDao.findUserByVo(vo);
        for (User user1 : users) {
            System.out.println(user1);
        }
    }

}
