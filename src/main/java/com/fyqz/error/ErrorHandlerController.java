package com.fyqz.error;

import com.fyqz.result.Result;
import com.fyqz.result.ResultUtil;
import com.netflix.zuul.context.RequestContext;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ErrorHandlerController implements ErrorController {
    /**
     * 出异常后进入该方法，交由下面的方法处理
     */
    @Override
    public String getErrorPath() {
        return "/error";
    }

    @RequestMapping("/error")
    public Result error() {
        RequestContext requestContext = RequestContext.getCurrentContext();
        System.out.println("2222");
        return ResultUtil.error();
    }
}