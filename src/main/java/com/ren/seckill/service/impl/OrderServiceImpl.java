package com.ren.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ren.seckill.exception.GlobalException;
import com.ren.seckill.mapper.OrderMapper;
import com.ren.seckill.pojo.Order;
import com.ren.seckill.pojo.SeckillGoods;
import com.ren.seckill.pojo.SeckillOrder;
import com.ren.seckill.pojo.User;
import com.ren.seckill.service.IGoodsService;
import com.ren.seckill.service.IOrderService;
import com.ren.seckill.service.ISeckillGoodsService;
import com.ren.seckill.service.ISeckillOrderService;
import com.ren.seckill.utils.MD5Util;
import com.ren.seckill.utils.UUIDUtil;
import com.ren.seckill.vo.pojo.GoodsVo;
import com.ren.seckill.vo.pojo.OrderVo;
import com.ren.seckill.vo.result.ResultBeanEnum;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Ren
 * @since 2021-11-17
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    @Autowired
    private ISeckillGoodsService seckillGoodsService;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ISeckillOrderService seckillOrderService;

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    @Transactional
    public Order doSeckillGoods(User user, GoodsVo goods) {
        // 秒杀商品表减库存
        SeckillGoods seckillGoods = seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>().eq("goods_id", goods.getId()));
        seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
        // seckillGoodsService.updateById(seckillGoods);

        // 解决库存超卖问题
        // boolean flag = seckillGoodsService.update(new UpdateWrapper<SeckillGoods>()
        //         .set("stock_count", seckillGoods.getStockCount())
        //         .eq("id", seckillGoods.getId())
        //         .gt("stock_count", 0)
        // );
        boolean flag = seckillGoodsService.update(new UpdateWrapper<SeckillGoods>().setSql("stock_count = stock_count - 1")
                .eq("goods_id", goods.getId()).gt("stock_count", 0));

        if(!flag) {
            return null;
        }

        // 生成订单
        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(goods.getId());
        order.setDeliveryAddrId(0L);
        order.setGoodsName(goods.getGoodsName());
        order.setGoodsPrice(seckillGoods.getSeckillPrice());
        order.setGoodsCount(1);
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());
        order.setPayDate(new Date());
        orderMapper.insert(order);
        // 生成秒杀订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setUserId(user.getId());
        seckillOrder.setGoodsId(goods.getId());
        seckillOrder.setOrderId(order.getId());
        seckillOrderService.save(seckillOrder);

        redisTemplate.opsForValue().set("order:" + user.getId() + ":" + goods.getId(), seckillOrder);

        return order;
    }

    @Override
    public OrderVo detail(Long orderId) {
        if(StringUtils.isEmpty(orderId)) {
            throw new GlobalException(ResultBeanEnum.ORDER_NOT_EXIST);
        }
        Order order = orderMapper.selectById(orderId);
        GoodsVo goodsVo = goodsService.selectGoodsVoByGoodsId(order.getGoodsId());
        OrderVo detail = new OrderVo();
        detail.setOrder(order);
        detail.setGoodsVo(goodsVo);

        return detail;
    }

    @Override
    public String createPath(User user, Long goodsId) {
        String url = MD5Util.md5(UUIDUtil.uuid() + "123456");
        // 存到redis中
        redisTemplate.opsForValue().set("seckillPath:" + user.getId() + ":" + goodsId, url, 60, TimeUnit.SECONDS);
        return url;
    }

    /**
     * 校验秒杀地址
     * @param user
     * @param goodsId
     * @return
     */
    @Override
    public boolean checkPath(User user, Long goodsId, String path) {
        if (user == null || goodsId < 0 || StringUtils.isEmpty(path)) {
            return false;
        }
        String redisPath = ((String) redisTemplate.opsForValue().get("seckillPath:" + user.getId() + ":" + goodsId));
        return redisPath.equals(path);
    }

    /**
     * 验证码校验
     * @param user
     * @param goodsId
     * @param captcha
     * @return
     */
    @Override
    public boolean checkCaptcha(User user, Long goodsId, String captcha) {
        if(StringUtils.isEmpty(captcha) || goodsId < 0 || user == null) {
            return false;
        }
        String redisCaptcha = (String) redisTemplate.opsForValue().get("captcha:" + user.getId() + ":" + goodsId);
        return redisCaptcha.equals(captcha);
    }
}
