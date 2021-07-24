package com.michael.security.distributed.order.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.michael.security.distributed.order.common.EncryptUtil;
import com.michael.security.distributed.order.model.UserDTO;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import sun.plugin.liveconnect.SecurityContextHelper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * desc
 *
 * @author wangce 2021-07-09
 * @since 1.0.0
 */
@Component
public class TokenAuthenticationFilter  extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        //解析出头中的token
        String token = httpServletRequest.getHeader("json-token");
        if(token != null) {
            //1.解析token
            String json = EncryptUtil.decodeUTF8StringBase64(token);
            JSONObject userJson = JSON.parseObject(json);
//            UserDTO user = new UserDTO();
            //用户身份信息
            String principal = userJson.getString("principal");
//            user.setUsername(principal);
            //将json转成对象
            UserDTO user = JSON.parseObject(principal, UserDTO.class);
            //用户权限
            JSONArray authoritiesArray = userJson.getJSONArray("authorities");
            String[] authorities = authoritiesArray.toArray(new String[authoritiesArray.size()]);

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user, null, AuthorityUtils.createAuthorityList(authorities));
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));

            //将authenticationToken保存进安全上下文
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        }
        //继续让过滤器往下执行
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
