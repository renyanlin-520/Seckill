package com.ren.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ren.seckill.pojo.Goods;
import com.ren.seckill.vo.pojo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Ren
 * @since 2021-11-17
 */
public interface IGoodsService extends IService<Goods> {

    List<GoodsVo> selectGoodsList();

    GoodsVo selectGoodsVoByGoodsId(Long goodsId);
}
