package com.fyqz.filter;

import com.fyqz.config.IgnoreProperties;
import com.fyqz.exception.BusinessException;
import com.fyqz.model.User;
import com.fyqz.result.Result;
import com.fyqz.rpc.UserServiceFeign;
import com.fyqz.util.DataUtil;
import com.fyqz.util.JwtUtils;
import com.fyqz.util.LogUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * zuul网关过滤器
 * zuul不仅只是路由，并且还能过滤，做一些安全验证。
 * 可以通过shouldFilter()方法返回值为false，来标明过滤器是否起作用
 */
@Slf4j
@Component
public class FyqzZuulFilter extends ZuulFilter {
    public static final String USER_KEY = "userId";

    @Resource
    private IgnoreProperties ignoreProperties;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserServiceFeign userServiceFeign;

    private static final Logger logger = LoggerFactory.getLogger(FyqzZuulFilter.class);

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 1;
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
        for (String str : ignoreProperties.getList()) {
            if (requestURI.contains(str)) {
                LogUtil.info(log, "该路径不校验Token,URI={}", requestURI);
                return null;
            }
        }
        String token = request.getHeader("TOKEN");
        //TOKEN为空
        if (StringUtils.isBlank(token)) {
            throw  new BusinessException(HttpStatus.UNAUTHORIZED.value(),"TOKEN不能为空");
        }
        //用户为空
        Claims claims = jwtUtils.getClaimByToken(token);
        if (claims == null || jwtUtils.isTokenExpired(claims.getExpiration())) {
            throw  new BusinessException(HttpStatus.UNAUTHORIZED.value(),"TOKEN失效，请重新登录");
        }
        // TOKEN失效
        Result result = userServiceFeign.queryUser(claims.getSubject());
        if (DataUtil.isNotEmpty(result) && DataUtil.isNotEmpty(result.getData())) {
            User user = (User) result.getData();
            if (!user.getToken().equals(token)) {
                throw  new BusinessException(HttpStatus.UNAUTHORIZED.value(),"TOKEN失效，请重新登录");
            }
        }
        request.setAttribute(USER_KEY, claims.getSubject());
        return null;
    }
}