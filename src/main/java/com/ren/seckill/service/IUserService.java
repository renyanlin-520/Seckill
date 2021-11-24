package com.ren.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ren.seckill.pojo.User;
import com.ren.seckill.vo.pojo.LoginVo;
import com.ren.seckill.vo.result.ResultBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Ren
 * @since 2021-11-05
 */
public interface IUserService extends IService<User> {

    ResultBean loginUser(LoginVo loginVo, HttpServletResponse response, HttpServletRequest request);

    User getUserByCookie(String userTicket, HttpServletResponse response, HttpServletRequest request);

    ResultBean updatePassword(String userTicket, String password, HttpServletResponse response, HttpServletRequest request);
}
