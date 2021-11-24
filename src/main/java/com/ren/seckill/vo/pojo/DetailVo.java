package com.ren.seckill.vo.pojo;

import com.ren.seckill.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * @author ren
 * @version 1.0
 * @date 2021/11/19 14:59
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetailVo {
    private User user;

    private GoodsVo goodsVo;

    private int seckillStatus;

    private int remainSeconds;
}
