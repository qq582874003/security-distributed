package com.michael.security.distributed.uaa.model;

import lombok.Data;

/**
 * desc
 *
 * @author wangce 2021-05-20
 * @since 1.0.0
 */
@Data
public class PermissionDto {
    /**
     * 权限id
     */
    private String id;

    /**
     * 标识
     */
    private String code;
    private String description;

    /**
     * 请求地址
     */
    private String url;
}
