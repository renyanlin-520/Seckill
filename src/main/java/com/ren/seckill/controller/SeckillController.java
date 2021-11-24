package com.ren.seckill.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ren.seckill.annotation.AccessLimit;
import com.ren.seckill.exception.GlobalException;
import com.ren.seckill.pojo.Order;
import com.ren.seckill.pojo.SeckillOrder;
import com.ren.seckill.pojo.User;
import com.ren.seckill.service.IGoodsService;
import com.ren.seckill.service.IOrderService;
import com.ren.seckill.service.ISeckillOrderService;
import com.ren.seckill.vo.pojo.GoodsVo;
import com.ren.seckill.vo.result.ResultBean;
import com.ren.seckill.vo.result.ResultBeanEnum;
import com.sun.org.apache.regexp.internal.RE;
import com.wf.captcha.ArithmeticCaptcha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author ren
 * @version 1.0
 * @date 2021/11/17 17:37
 */
@Slf4j
@Controller
@RequestMapping("/sk/seckill")
public class SeckillController {

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private IOrderService orderService;

    @Autowired
    private ISeckillOrderService seckillOrderService;

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping("/doSeckill2")
    public String doSeckill2(Model model, User user, Long goodsId) {
        if(user == null) {
            return "login";
        }
        model.addAttribute("user", user);
        GoodsVo goods = goodsService.selectGoodsVoByGoodsId(goodsId);
        // 判断是否有库存
        if(goods.getStockCount() < 1) {
            model.addAttribute("errMsg", ResultBeanEnum.EMPTY_STOCK.getMessage());
            return "error";
        }
        // 判断是否重复抢购
        SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));
        if(seckillOrder != null) {
            model.addAttribute("errMsg", ResultBeanEnum.REPEAT_STOCK.getMessage());
            return "error";
        }
        // 秒杀，抢购商品
        Order order = orderService.doSeckillGoods(user, goods);
        model.addAttribute("order", order);
        model.addAttribute("goods", goods);
        return "order/detail";
    }

    /**
     * 获取秒杀地址
     * @param user
     * @return
     */
    @AccessLimit(second=5, maxCount=5, needLogin=true)
    @RequestMapping(value = "/path", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getPath(User user, Long goodsId, String captcha, HttpServletRequest request) {
        if(user == null) {
            return ResultBean.error(ResultBeanEnum.SESSION_ERROR);
        }

        // 限制访问次数，五秒内访问五次
        // ValueOperations valueOperations = redisTemplate.opsForValue();
        // String uri = request.getRequestURI();
        // captcha = "0";
        // Integer count = (Integer) valueOperations.get(uri + ":" + user.getId());
        // if(count == null) {
        //     valueOperations.set(uri + ":" + user.getId(), 1, 5, TimeUnit.SECONDS);
        // } else if(count < 5) {
        //     valueOperations.increment(uri + ":" + user.getId());
        // } else {
        //     return ResultBean.error(ResultBeanEnum.ACCESS_LIMIT_REAHCED);
        // }

        boolean check = orderService.checkCaptcha(user, goodsId, captcha);
        if(!check) {
            return ResultBean.error(ResultBeanEnum.CAPTCHA_ERROR);
        }
        String url = orderService.createPath(user, goodsId);
        return ResultBean.success(url);
    }

    @ResponseBody
    @RequestMapping(value = "/{path}/doSeckill", method = RequestMethod.POST)
    public ResultBean doSeckill(@PathVariable("path") String path, Model model, User user, Long goodsId) {
        if(user == null) {
            return ResultBean.error(ResultBeanEnum.SESSION_ERROR);
        }
        GoodsVo goods = goodsService.selectGoodsVoByGoodsId(goodsId);
        // 判断是否有库存
        if(goods.getStockCount() < 1) {
            model.addAttribute("errMsg", ResultBeanEnum.EMPTY_STOCK.getMessage());
            return ResultBean.error(ResultBeanEnum.EMPTY_STOCK);
        }

        // 判断路径
        boolean check = orderService.checkPath(user, goodsId, path);
        if(!check) {
            return ResultBean.error(ResultBeanEnum.REQUEST_ILLEGAL);
        }

        // 判断是否重复抢购
        // SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));

        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goods.getId());

        if(seckillOrder != null) {
            model.addAttribute("errMsg", ResultBeanEnum.REPEAT_STOCK.getMessage());
            return ResultBean.error(ResultBeanEnum.REPEAT_STOCK);
        }
        // 秒杀，抢购商品
        Order order = orderService.doSeckillGoods(user, goods);
        return ResultBean.success(order);
    }

    /**
     * 获取验证码
     * @param user
     * @param goodsId
     * @param response
     */
    @RequestMapping(value = "/captcha", method = RequestMethod.GET)
    public void verifyCode(User user, Long goodsId, HttpServletResponse response) {
        if(user == null || goodsId < 0) {
            throw new GlobalException(ResultBeanEnum.REQUEST_ILLEGAL);
        }
        // 设置请求头为输出格式的类型
        response.setContentType("image/jpg");
        response.setHeader("Pagram", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        // 生成验证码，将结果放入Redis
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 32, 3);
        redisTemplate.opsForValue().set("captcha:" + user.getId() + ":" + goodsId, captcha.text(), 300, TimeUnit.SECONDS);
        try {
            captcha.out(response.getOutputStream());
        } catch (IOException e) {
            log.error("验证码生成失败：", e.getMessage());
        }
    }
}
