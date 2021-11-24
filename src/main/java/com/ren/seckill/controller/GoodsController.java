package com.ren.seckill.controller;

import com.ren.seckill.pojo.Goods;
import com.ren.seckill.pojo.User;
import com.ren.seckill.service.IGoodsService;
import com.ren.seckill.service.IUserService;
import com.ren.seckill.vo.pojo.DetailVo;
import com.ren.seckill.vo.pojo.GoodsVo;
import com.ren.seckill.vo.result.ResultBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author ren
 * @version 1.0
 * @date 2021/11/11 15:53
 */
@Controller
@RequestMapping("/sk/goods")
public class GoodsController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;

    /**
     * 跳转到商品列表页面
     * @param user
     * @param model
     * @return
     */
    @RequestMapping(value = "/toList", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toList(User user, Model model, HttpServletResponse response, HttpServletRequest request) {
        // if (StringUtils.isEmpty(ticket)) {
        //     return "login";
        // }
        // 不从HttpSession中获取user对象
        // User user = (User) session.getAttribute(ticket);

        // 从cookie中获取对象
        // User user = userService.getUserByCookie(ticket, response, request);
        //
        // if (user == null) {
        //     return "login";
        // }

        // 页面缓存
        // 1.Redis 中获取页面数据，如果不为空，直接返回页面
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = ((String) valueOperations.get("goods/list"));
        if(!StringUtils.isEmpty(html)) {
            return html;
        }

        // 获取商品数据
        List<GoodsVo> goodsList = goodsService.selectGoodsList();
        model.addAttribute("user", user);
        model.addAttribute("goodsList", goodsList);

        // 2.如果为空的话，就手动渲染，存入redis中并返回
        WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods/list", webContext);
        if(!StringUtils.isEmpty(html)) {
            valueOperations.set("goods/list", html, 60, TimeUnit.SECONDS);
        }

        // return "goods/list";   // 返回页面路劲
        return html;
    }

    @RequestMapping(value = "/toDetail/{goodsId}", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toDetail(Model model, User user, @PathVariable("goodsId") Long goodsId, HttpServletResponse response, HttpServletRequest request) {
        // 1.判断获取redis中的页面，如果不为空，有就直接返回
        String html = (String) redisTemplate.opsForValue().get("goods/detail:" + goodsId);
        if(!StringUtils.isEmpty(html)) {
            return html;
        }
        model.addAttribute("user", user);
        GoodsVo goods = goodsService.selectGoodsVoByGoodsId(goodsId);
        Date startDate = goods.getStartDate();
        Date endDate = goods.getEndDate();
        Date nowDate = new Date();
        // 秒杀状态判断
        int seckillStatus;
        // 秒杀倒计时
        int remainSeconds = 0;
        if(nowDate.before(startDate)) {
            // 秒杀还未开始
            seckillStatus = 0;
            remainSeconds = ((int) ((startDate.getTime() - nowDate.getTime()) / 1000));
        } else if(nowDate.after(endDate)) {
            // 秒杀已结束
            seckillStatus = 2;
            remainSeconds = -1;
        } else {
            // 秒杀进行中
            seckillStatus = 1;
            remainSeconds = 0;
        }
        model.addAttribute("remainSeconds", remainSeconds);
        model.addAttribute("seckillStatus", seckillStatus);
        model.addAttribute("goods", goods);

        // 2.如果为空就手动渲染，然后返回
        WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods/detail", webContext);
        if(!StringUtils.isEmpty(html)) {
            redisTemplate.opsForValue().set("goods/detail:" + goodsId, html, 60, TimeUnit.SECONDS);
        }
        // return "goods/detail";
        return html;
    }

    @ResponseBody
    @RequestMapping(value = "/detail/{goodsId}", method = RequestMethod.GET)
    public ResultBean detail(User user, @PathVariable("goodsId") Long goodsId) {

        GoodsVo goods = goodsService.selectGoodsVoByGoodsId(goodsId);
        Date startDate = goods.getStartDate();
        Date endDate = goods.getEndDate();
        Date nowDate = new Date();
        // 秒杀状态判断
        int seckillStatus;
        // 秒杀倒计时
        int remainSeconds = 0;
        if(nowDate.before(startDate)) {
            // 秒杀还未开始
            seckillStatus = 0;
            remainSeconds = ((int) ((startDate.getTime() - nowDate.getTime()) / 1000));
        } else if(nowDate.after(endDate)) {
            // 秒杀已结束
            seckillStatus = 2;
            remainSeconds = -1;
        } else {
            // 秒杀进行中
            seckillStatus = 1;
            remainSeconds = 0;
        }
        DetailVo detail = new DetailVo();
        detail.setUser(user);
        detail.setGoodsVo(goods);
        detail.setSeckillStatus(seckillStatus);
        detail.setRemainSeconds(remainSeconds);
        return ResultBean.success(detail);
    }
}
