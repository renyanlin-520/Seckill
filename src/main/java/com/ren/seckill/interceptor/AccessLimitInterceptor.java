package com.ren.seckill.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ren.seckill.annotation.AccessLimit;
import com.ren.seckill.config.UserContext;
import com.ren.seckill.pojo.User;
import com.ren.seckill.service.IUserService;
import com.ren.seckill.utils.CookieUtil;
import com.ren.seckill.vo.result.ResultBean;
import com.ren.seckill.vo.result.ResultBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

/**
 * 接口限流拦截器
 * @author ren
 * @version 1.0
 * @date 2021/11/23 11:28
 */
@Component
public class AccessLimitInterceptor implements HandlerInterceptor {

    @Autowired
    private IUserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 执行之前处理的方法
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler instanceof HandlerMethod) {
            User user = getUser(request, response);
            UserContext.setUser(user);
            HandlerMethod hm = (HandlerMethod) handler;
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            if (accessLimit == null) {
                return true;
            }
            int second = accessLimit.second();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();
            String uri = request.getRequestURI();
            if(needLogin) {
                if(user == null) {
                    // 构建返回对象
                    render(response, ResultBeanEnum.SESSION_ERROR);
                    return false;
                }
                uri += ":" + user.getId();
            }
            ValueOperations valueOperations = redisTemplate.opsForValue();
            Integer count = (Integer) valueOperations.get(uri);
            if(count == null) {
                valueOperations.set(uri, 1, second, TimeUnit.SECONDS);
            } else if (count < maxCount) {
                valueOperations.increment(uri);
            } else {
                render(response, ResultBeanEnum.ACCESS_LIMIT_REAHCED);;
                return false;
            }

        }
        return true;
    }

    /**
     * 构建返回对象
     * @param response
     * @param resultBeanEnum
     */
    private void render(HttpServletResponse response, ResultBeanEnum resultBeanEnum) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        ResultBean resultBean = ResultBean.error(resultBeanEnum);
        out.write(new ObjectMapper().writeValueAsString(resultBean));
        out.flush();
        out.close();
    }

    /**
     * 获取用户
     * @param request
     * @param response
     * @return
     */
    private User getUser(HttpServletRequest request, HttpServletResponse response) {
        String ticket = CookieUtil.getCookieValue(request, "userTicket");
        if(StringUtils.isEmpty(ticket))
            return null;
        return userService.getUserByCookie(ticket, response, request);
    }
}
