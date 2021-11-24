package com.ren.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ren.seckill.pojo.Goods;
import com.ren.seckill.vo.pojo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Ren
 * @since 2021-11-17
 */
public interface GoodsMapper extends BaseMapper<Goods> {

    List<GoodsVo> selectGoodsList();

    GoodsVo selectGoodsVoByGoodsId(Long goodsId);
}
