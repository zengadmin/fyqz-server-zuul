package com.fyqz.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        if (requestURI.contains("api-docs")) {
            return null;
        }
        logger.info("send{} request to {}", request.getMethod(), request.getRequestURI().toCharArray());
        String accessToken = request.getHeader("TOKEN");
        if (StringUtils.isBlank(accessToken)) {
            logger.warn("accessToken is empty");
            currentContext.setSendZuulResponse(false);
            currentContext.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
            return null;
        }
        logger.info("accessToken {}", accessToken);
        return null;
    }
}