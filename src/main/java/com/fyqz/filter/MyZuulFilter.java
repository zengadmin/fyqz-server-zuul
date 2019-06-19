package com.fyqz.filter;

import com.fyqz.exception.BusinessException;
import com.fyqz.util.JwtUtils;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * zuul网关过滤器
 * zuul不仅只是路由，并且还能过滤，做一些安全验证。
 * 可以通过shouldFilter()方法返回值为false，来标明过滤器是否起作用
 */
@Component
public class MyZuulFilter extends ZuulFilter {
    public static final String USER_KEY = "userId";
    @Autowired
    private JwtUtils jwtUtils;

    private static final Logger logger = LoggerFactory.getLogger(MyZuulFilter.class);

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletRequest request = currentContext.getRequest();
        String requestURI = request.getRequestURI();
        if (requestURI.contains("api-docs")||requestURI.contains("userLogin")) {
            return null;
        }
        String token = request.getHeader("TOKEN");
        //凭证为空
        if(StringUtils.isBlank(token)){
            throw new BusinessException( HttpStatus.UNAUTHORIZED.value(),"TOKEN不能为空");
        }
        Claims claims = jwtUtils.getClaimByToken(token);
        if(claims == null || jwtUtils.isTokenExpired(claims.getExpiration())){
            throw new BusinessException( HttpStatus.UNAUTHORIZED.value(),"TOKEN失效，请重新登录");
        }
        request.setAttribute(USER_KEY, claims.getSubject());
        return null;
    }
}