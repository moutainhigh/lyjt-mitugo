/**
 * Copyright (C) 2018-2020
 */
package co.yixiang.modules.shop.service.impl;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.common.web.vo.Paging;
import co.yixiang.enums.CommonEnum;
import co.yixiang.modules.shop.entity.YxSystemStore;
import co.yixiang.modules.shop.mapper.YxSystemStoreMapper;
import co.yixiang.modules.shop.mapping.SystemStoreMap;
import co.yixiang.modules.shop.service.YxSystemStoreService;
import co.yixiang.modules.shop.web.param.YxSystemStoreQueryParam;
import co.yixiang.modules.shop.web.vo.YxSystemStoreQueryVo;
import co.yixiang.mp.config.ShopKeyUtils;
import co.yixiang.utils.LocationUtils;
import co.yixiang.utils.RedisUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;


/**
 * <p>
 * 门店自提 服务实现类
 * </p>
 *
 * @author hupeng
 * @since 2020-03-04
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class YxSystemStoreServiceImpl extends BaseServiceImpl<YxSystemStoreMapper, YxSystemStore> implements YxSystemStoreService {

    @Autowired
    private YxSystemStoreMapper yxSystemStoreMapper;

    @Autowired
    private SystemStoreMap storeMap;

    @Override
    public List<YxSystemStoreQueryVo> getStoreList(String latitude, String longitude, int page, int limit) {

        Page<YxSystemStore> pageModel = new Page<>(page, limit);
        List<YxSystemStoreQueryVo> list = yxSystemStoreMapper.getStoreList(pageModel,Double.valueOf(longitude),Double.valueOf(latitude));
        list.forEach(item->{
            if(StringUtils.isNotBlank(latitude) && StringUtils.isNotBlank(longitude)) {
                String newDis = NumberUtil.round(Double.valueOf(item.getDistance()) / 1000,2).toString();
                item.setDistance(newDis + "km");
            } else {
                item.setDistance("");
            }
        });
        return list;
    }

    @Override
    public YxSystemStoreQueryVo getStoreInfo(String latitude,String longitude) {
        YxSystemStore systemStore = new YxSystemStore();
        systemStore.setIsDel(CommonEnum.DEL_STATUS_0.getValue());
        systemStore.setIsShow(CommonEnum.SHOW_STATUS_1.getValue());
        YxSystemStore yxSystemStore = yxSystemStoreMapper.selectOne(
                Wrappers
                .query(systemStore)
                .orderByDesc("id")
                .last("limit 1"));
        if(yxSystemStore == null) return new YxSystemStoreQueryVo();
        String mention = RedisUtil.get(ShopKeyUtils.getStoreSelfMention());
        if(mention == null || Integer.valueOf(mention) == 2) return new YxSystemStoreQueryVo();
        YxSystemStoreQueryVo systemStoreQueryVo = storeMap.toDto(yxSystemStore);
        if(StrUtil.isNotEmpty(latitude) && StrUtil.isNotEmpty(longitude)){
            double distance = LocationUtils.getDistance(Double.valueOf(latitude),Double.valueOf(longitude),
                    Double.valueOf(yxSystemStore.getLatitude()),Double.valueOf(yxSystemStore.getLongitude()));
            systemStoreQueryVo.setDistance(new BigDecimal(distance).divide(new BigDecimal(1000), 2, BigDecimal.ROUND_HALF_UP).toString() + "km");
        }
        return systemStoreQueryVo;
    }

    @Override
    public YxSystemStoreQueryVo getYxSystemStoreById(Serializable id){
        return yxSystemStoreMapper.getYxSystemStoreById(id);
    }

    @Override
    public Paging<YxSystemStoreQueryVo> getYxSystemStorePageList(YxSystemStoreQueryParam yxSystemStoreQueryParam){
        Page page = setPageParam(yxSystemStoreQueryParam,OrderItem.desc("add_time"));
        IPage<YxSystemStoreQueryVo> iPage = yxSystemStoreMapper.getYxSystemStorePageList(page,yxSystemStoreQueryParam);
        return new Paging(iPage);
    }

}
