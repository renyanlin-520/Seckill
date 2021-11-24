package com.ren.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ren.seckill.pojo.Order;
import com.ren.seckill.pojo.User;
import com.ren.seckill.vo.pojo.GoodsVo;
import com.ren.seckill.vo.pojo.OrderVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Ren
 * @since 2021-11-17
 */
public interface IOrderService extends IService<Order> {

    Order doSeckillGoods(User user, GoodsVo goods);

    OrderVo detail(Long orderId);

    String createPath(User user, Long goodsId);

    boolean checkPath(User user, Long goodsId, String path);

    boolean checkCaptcha(User user, Long goodsId, String captcha);
}
