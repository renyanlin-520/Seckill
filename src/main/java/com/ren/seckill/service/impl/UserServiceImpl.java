package com.ren.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ren.seckill.exception.GlobalException;
import com.ren.seckill.mapper.UserMapper;
import com.ren.seckill.pojo.User;
import com.ren.seckill.service.IUserService;
import com.ren.seckill.utils.CookieUtil;
import com.ren.seckill.utils.MD5Util;
import com.ren.seckill.utils.UUIDUtil;
import com.ren.seckill.vo.pojo.LoginVo;
import com.ren.seckill.vo.result.ResultBean;
import com.ren.seckill.vo.result.ResultBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Ren
 * @since 2021-11-05
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public ResultBean loginUser(LoginVo loginVo, HttpServletResponse response, HttpServletRequest request) {
        String userName = loginVo.getUserName();
        String password = loginVo.getPassword();
        // 判断用户名与密码不能为空
        // if(ObjectUtils.isEmpty(userName) || ObjectUtils.isEmpty(password)) {
        //     return ResultBean.error(ResultBeanEnum.LOGIN_ERROR);
        // }
        // 用户名手机号码校验
        // if(!MobileUtil.isMobile(userName)) {
        //     return ResultBean.error(ResultBeanEnum.LOGIN_MOBILE_ERROR);
        // }
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("user_name", userName);
        User user = userMapper.selectOne(wrapper);
        if (ObjectUtils.isEmpty(user)) {
            // return ResultBean.error(ResultBeanEnum.LOGIN_USER_ERROR);
            // 变成直接抛异常的形式
            throw new GlobalException(ResultBeanEnum.LOGIN_USER_ERROR);
        }

        // 判断密码是否正确
        if (!MD5Util.fromPassToDBPass(password, user.getSalt()).equals(user.getPassword())) {
            // return ResultBean.error(ResultBeanEnum.LOGIN_ERROR);
            // 变成直接抛异常的形式
            throw new GlobalException(ResultBeanEnum.LOGIN_ERROR);
        }
        // 用户登录次数+1
        if(user.getLoginCount() == null) {
            user.setLoginCount(0);
        }
        user.setLoginCount(user.getLoginCount() + 1);
        user.setLastLoginDate(new Date());
        userMapper.updateById(user);

        // 生成Cookie值, 并将用户对象存到session中去
        String ticket = UUIDUtil.uuid();
        // 将用户对象存到spring session中去
        // request.getSession().setAttribute(ticket, user);

        // 将用户信息设置到redis中去
        redisTemplate.opsForValue().set("user:" + ticket, user);
        CookieUtil.setCookie(request, response, "userTicket", ticket);
        return ResultBean.success(user);
    }

    /**
     * 根据cookie获取用户对象
     * @param userTicket
     * @return
     */
    @Override
    public User getUserByCookie(String userTicket, HttpServletResponse response, HttpServletRequest request) {
        if(StringUtils.isEmpty(userTicket)) {
            return null;
        }
        User user = (User) redisTemplate.opsForValue().get("user:" + userTicket);
        if(user != null) {
            CookieUtil.setCookie(request, response, "userTicket", userTicket);
        }
        return user;
    }

    /**
     * 更新密码
     * @param userTicket
     * @param password
     * @param response
     * @param request
     * @return
     */
    @Override
    public ResultBean updatePassword(String userTicket, String password, HttpServletResponse response, HttpServletRequest request) {
        User user = getUserByCookie(userTicket, response, request);
        if(user == null) {
            throw new GlobalException(ResultBeanEnum.MOBILE_NOT_EXIST);
        }
        user.setPassword(MD5Util.inputPassToDBPass(password, user.getSalt()));
        int flag = userMapper.updateById(user);
        if(flag == 1) {
            // 删除去redis中的user对象
            redisTemplate.delete("user:" + userTicket);
            return ResultBean.success();
        }
        return ResultBean.error(ResultBeanEnum.PASSWORD_UPDATE_FAILED);
    }
}
