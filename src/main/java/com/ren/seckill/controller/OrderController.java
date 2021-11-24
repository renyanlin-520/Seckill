package com.ren.seckill.controller;


import com.ren.seckill.pojo.User;
import com.ren.seckill.service.IOrderService;
import com.ren.seckill.vo.pojo.OrderVo;
import com.ren.seckill.vo.result.ResultBean;
import com.ren.seckill.vo.result.ResultBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Ren
 * @since 2021-11-17
 */
@Controller
@RequestMapping("/sk/order")
public class OrderController {

    @Autowired
    private IOrderService orderService;

    @ResponseBody
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public ResultBean detail(User user, Long orderId) {
        if(user == null) {
            return ResultBean.error(ResultBeanEnum.SESSION_ERROR);
        }
        OrderVo detail = orderService.detail(orderId);
        return ResultBean.success(detail);
    }
}
