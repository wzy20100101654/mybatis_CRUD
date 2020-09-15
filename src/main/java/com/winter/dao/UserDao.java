package com.winter.dao;

import com.winter.domain.User;

import java.util.List;

//用户持久层接口
public interface UserDao {
    //查询所有用户
    List<User> findAll();
    //保存用户
    void saveUser(User user);
    //修改用户
    void updateUser(User user);
    //删除用户,根据ID
    void deleteUser(Integer userId);
    //根据id查询用户
    User findById(Integer userId);
    //模糊查询
    List<User> findByName(String username);
    //查询总用户数
    int findTotal();
}
