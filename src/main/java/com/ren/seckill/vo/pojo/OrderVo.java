package com.ren.seckill.vo.pojo;

import com.ren.seckill.pojo.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 订单详情返回对象
 * @author ren
 * @version 1.0
 * @date 2021/11/19 16:45
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderVo {
    private Order order;
    private GoodsVo goodsVo;
}
