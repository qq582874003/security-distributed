package com.michael.security.distributed.order.controller;

import com.michael.security.distributed.order.model.UserDTO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * desc
 *
 * @author wangce 2021-07-05
 * @since 1.0.0
 */
@RestController
public class OrderController {

    @GetMapping(value = "r1")
    @PreAuthorize("hasAnyAuthority('p1')") //拥有p1权限方可访问此url
    public String r1() {
        //获取用户身份信息
        UserDTO principal = (UserDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getUsername() + "访问资源1";
    }
}
