package com.ren.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ren.seckill.mapper.GoodsMapper;
import com.ren.seckill.pojo.Goods;
import com.ren.seckill.service.IGoodsService;
import com.ren.seckill.vo.pojo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Ren
 * @since 2021-11-17
 */
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements IGoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Override
    public List<GoodsVo> selectGoodsList() {
        return goodsMapper.selectGoodsList();
    }

    @Override
    public GoodsVo selectGoodsVoByGoodsId(Long goodsId) {
        return goodsMapper.selectGoodsVoByGoodsId(goodsId);
    }
}
