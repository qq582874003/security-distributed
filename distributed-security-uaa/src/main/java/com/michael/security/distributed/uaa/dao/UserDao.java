package com.michael.security.distributed.uaa.dao;

import com.michael.security.distributed.uaa.model.PermissionDto;
import com.michael.security.distributed.uaa.model.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * desc
 *
 * @author wangce 2021-05-20
 * @since 1.0.0
 */
@Repository
public class UserDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    /**
     * 根据用户名获取用户信息
     * @param username
     * @return
     */
    public UserDto getUserByUsername(String username){
        String sql = "select id,username,password,fullname from t_user where username = ?";
        List<UserDto> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(UserDto.class), username);
        if (CollectionUtils.isEmpty(list)){
            return null;
        }
        return list.get(0);
    }

    /**
     * 根据用户id查询用户权限
     * @param userId
     * @return
     */
    public List<String> findPermissionsByUserId(String userId){
        String sql = "SELECT * FROM t_permission WHERE id IN("
                +"SELECT permission_id FROM t_role_permission WHERE role_id IN("
                +"SELECT role_id FROM t_user_role WHERE user_id = ?))";
        List<PermissionDto> list =  jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(PermissionDto.class), userId);
        List<String> permissions = new ArrayList<>();
        list.iterator().forEachRemaining(permissionDto -> permissions.add(permissionDto.getCode()));
        return permissions;
    }

}
