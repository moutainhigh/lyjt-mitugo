/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.coupon.service.impl;

import co.yixiang.modules.coupon.domain.YxCoupons;
import co.yixiang.common.service.impl.BaseServiceImpl;
import lombok.AllArgsConstructor;
import co.yixiang.dozer.service.IGenerator;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.utils.ValidationUtil;
import co.yixiang.utils.FileUtil;
import co.yixiang.modules.coupon.service.YxCouponsService;
import co.yixiang.modules.coupon.service.dto.YxCouponsDto;
import co.yixiang.modules.coupon.service.dto.YxCouponsQueryCriteria;
import co.yixiang.modules.coupon.service.mapper.YxCouponsMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
// 默认不使用缓存
//import org.springframework.cache.annotation.CacheConfig;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
* @author huiy
* @date 2020-08-14
*/
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "yxCoupons")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class YxCouponsServiceImpl extends BaseServiceImpl<YxCouponsMapper, YxCoupons> implements YxCouponsService {

    private final IGenerator generator;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(YxCouponsQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<YxCoupons> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), YxCouponsDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<YxCoupons> queryAll(YxCouponsQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(YxCoupons.class, criteria));
    }


    @Override
    public void download(List<YxCouponsDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (YxCouponsDto yxCoupons : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("卡券编号", yxCoupons.getCouponNum());
            map.put("卡券名称", yxCoupons.getCouponName());
            map.put("卡券类型;1:代金券, 2:折扣券, 3:满减券", yxCoupons.getCouponType());
            map.put("卡券所属分类", yxCoupons.getCouponCategory());
            map.put("代金券面额, coupon_type为1时使用", yxCoupons.getDenomination());
            map.put("折扣券折扣率, coupon_type为2时使用", yxCoupons.getDiscount());
            map.put("使用门槛, coupon_type为3时使用", yxCoupons.getThreshold());
            map.put("优惠金额, coupon_type为3时使用", yxCoupons.getDiscountAmount());
            map.put("销售价格", yxCoupons.getSellingPrice());
            map.put("原价", yxCoupons.getOriginalPrice());
            map.put("平台结算价", yxCoupons.getSettlementPrice());
            map.put("佣金", yxCoupons.getCommission());
            map.put("每人限购数量", yxCoupons.getQuantityLimit());
            map.put("库存", yxCoupons.getInventory());
            map.put("销量", yxCoupons.getSales());
            map.put("虚拟销量", yxCoupons.getFicti());
            map.put("核销次数", yxCoupons.getWriteOff());
            map.put("有效期始", yxCoupons.getExpireDateStart());
            map.put("有效期止", yxCoupons.getExpireDateEnd());
            map.put("热门优惠; 1:是, 0否", yxCoupons.getIsHot());
            map.put("状态（0：未上架，1：上架）", yxCoupons.getIsShow());
            map.put("过期退 0:不支持 1支持", yxCoupons.getOuttimeRefund());
            map.put("免预约 0:不支持 1支持", yxCoupons.getNeedOrder());
            map.put("随时退 0:不支持 1支持", yxCoupons.getAwaysRefund());
            map.put("使用条件 描述", yxCoupons.getUseCondition());
            map.put("可用时间始", yxCoupons.getAvailableTimeStart());
            map.put("可用时间止", yxCoupons.getAvailableTimeEnd());
            map.put("是否删除（0：未删除，1：已删除）", yxCoupons.getDelFlag());
            map.put("创建人 根据创建人关联店铺", yxCoupons.getCreateUserId());
            map.put("修改人", yxCoupons.getUpdateUserId());
            map.put("创建时间", yxCoupons.getCreateTime());
            map.put("更新时间", yxCoupons.getUpdateTime());
            map.put("卡券详情", yxCoupons.getContent());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}