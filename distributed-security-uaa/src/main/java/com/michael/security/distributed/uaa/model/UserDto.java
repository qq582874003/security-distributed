package com.michael.security.distributed.uaa.model;

import lombok.Data;

/**
 * desc
 *
 * @author wangce 2021-05-20
 * @since 1.0.0
 */
@Data
public class UserDto {
    private String id;
    private String username;
    private String password;
    private String fullname;
    private String mobile;
}
