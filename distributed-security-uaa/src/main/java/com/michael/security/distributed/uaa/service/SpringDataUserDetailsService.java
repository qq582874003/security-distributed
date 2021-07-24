package com.michael.security.distributed.uaa.service;

import com.alibaba.fastjson.JSON;
import com.michael.security.distributed.uaa.dao.UserDao;
import com.michael.security.distributed.uaa.model.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * desc
 *
 * @author wangce 2021-05-20
 * @since 1.0.0
 */
@Service
public class SpringDataUserDetailsService implements UserDetailsService {

    @Autowired
    UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //登录账号
        System.out.println("username="+username);
        //根据账号去数据库查询...
        UserDto user = userDao.getUserByUsername(username);
        if(user == null){
            return null;
        }
        //无权限时，用静态
        //这里暂时使用静态数据
//        UserDetails userDetails = User.withUsername(user.getFullname()).password(user.getPassword()).authorities("p1").build();

        //有权限时，从数据库获取权限
        //查询用户权限
        List<String> permissions = userDao.findPermissionsByUserId(user.getId());
        String[] perarray = new String[permissions.size()];
        permissions.toArray(perarray);

        //创建userDetails
//        UserDetails userDetails = User.withUsername(user.getFullname()).password(user.getPassword()).authorities("p1").build();

        //这里将user转为json，将整体user存入userDetails
        String principal = JSON.toJSONString(user);
        UserDetails userDetails = User.withUsername(principal).password(user.getPassword()).authorities(perarray).build();
        return userDetails;
    }
}
