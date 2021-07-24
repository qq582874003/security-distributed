package com.michael.security.distributed.gateway.filter;

/**
 * desc
 *
 * @author wangce 2021-07-09
 * @since 1.0.0
 */

import com.alibaba.fastjson.JSON;
import com.michael.security.distributed.gateway.common.EncryptUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import sun.plugin.liveconnect.SecurityContextHelper;

import java.util.*;

/**
 * token传递拦截
 */

public class AuthFilter extends ZuulFilter {

    /**
     * 开启拦截
     * @return
     */
    @Override
    public boolean shouldFilter() {
        return true;
    }

    /**
     * 拦截类型
     * @return
     */
    @Override
    public String filterType() {
        //请求前
        return "pre";
    }

    @Override
    public int filterOrder() {
        //最优先
        return 0;
    }

    @Override
    public Object run() throws ZuulException {

        RequestContext cxt = RequestContext.getCurrentContext();
        //从安全上下文中拿到用户身份对象
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof OAuth2Authentication)) {
            //无token访问网关内资源的情况，目前仅有uaa服务直接暴露
            return null;
        }
        OAuth2Authentication oAuth2Authentication = (OAuth2Authentication)authentication;
        Authentication userAuthentication = oAuth2Authentication.getUserAuthentication();
        //取出用户身份信息
        String principal = userAuthentication.getName();
        //取出用户的权限
        List<String> authorities = new ArrayList<>();
        //从userAuthentication取出权限，放在authorities中
        userAuthentication.getAuthorities().forEach(c -> authorities.add(c.getAuthority()));
        //获取其他的请求信息
        OAuth2Request oAuth2Request = oAuth2Authentication.getOAuth2Request();
        Map<String, String> requestParameters = oAuth2Request.getRequestParameters();
        Map<String, Object> jsonToken = new HashMap<>(requestParameters);

        if(userAuthentication != null){
            jsonToken.put("principal", principal);
            jsonToken.put("authorities", authorities);
        }
        //把身份信息和权限信息放在json中，加入http的header中，转发给微服务
        cxt.addZuulRequestHeader("json-token", EncryptUtil.encodeUTF8StringBase64(JSON.toJSONString(jsonToken)));

        return null;
    }
}
