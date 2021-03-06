/**
 * Copyright (C) 2018-2020
 * All rights reserved, Designed By www.yixiang.co
 */
package co.yixiang.modules.shop.service.impl;

import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.dozer.service.IGenerator;
import co.yixiang.modules.shop.domain.User;
import co.yixiang.modules.shop.domain.YxStoreProduct;
import co.yixiang.modules.shop.domain.YxStoreProductReply;
import co.yixiang.modules.shop.service.UserService;
import co.yixiang.modules.shop.service.YxStoreProductReplyService;
import co.yixiang.modules.shop.service.YxStoreProductService;
import co.yixiang.modules.shop.service.YxUserService;
import co.yixiang.modules.shop.service.dto.YxStoreProductReplyDto;
import co.yixiang.modules.shop.service.dto.YxStoreProductReplyQueryCriteria;
import co.yixiang.modules.shop.service.mapper.StoreProductReplyMapper;
import co.yixiang.utils.FileUtil;
import co.yixiang.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// 默认不使用缓存
//import org.springframework.cache.annotation.CacheConfig;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.cache.annotation.Cacheable;

/**
 * @author hupeng
 * @date 2020-05-12
 */
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "yxStoreProductReply")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class YxStoreProductReplyServiceImpl extends BaseServiceImpl<StoreProductReplyMapper, YxStoreProductReply> implements YxStoreProductReplyService {

    @Autowired
    private IGenerator generator;
    @Autowired
    private YxUserService yxUserService;
    @Autowired
    private YxStoreProductService yxStoreProductService;
    @Autowired
    private UserService userService;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(YxStoreProductReplyQueryCriteria criteria, Pageable pageable) {
//        getPage(pageable);
//        PageInfo<YxStoreProductReply> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        QueryWrapper<YxStoreProductReply> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByDesc(YxStoreProductReply::getAddTime);
        if (0 != criteria.getUserRole()) {
            if (null == criteria.getChildUser() || criteria.getChildUser().size() <= 0) {
                map.put("content", new ArrayList<>());
                map.put("totalElements", 0);
                return map;
            }
            queryWrapper.lambda().in(YxStoreProductReply::getMerId, criteria.getChildUser());
        }
        if (null != criteria.getIsReply()) {
            queryWrapper.lambda().eq(YxStoreProductReply::getIsReply, criteria.getIsReply());
        }
        if (StringUtils.isNotBlank(criteria.getStoreName())) {
            List<YxStoreProduct> list = this.yxStoreProductService.list(new QueryWrapper<YxStoreProduct>().lambda().eq(YxStoreProduct::getStoreName, criteria.getStoreName()));
            if (null == list || list.size() <= 0) {
                map.put("content", new ArrayList<>());
                map.put("totalElements", 0);
                return map;
            }
            List<Integer> ids = new ArrayList<>();
            for (YxStoreProduct item : list) {
                ids.add(item.getId());
            }
            queryWrapper.lambda().in(YxStoreProductReply::getProductId, ids);
        }
        if (StringUtils.isNotBlank(criteria.getUsername())) {
            User user = this.userService.getOne(new QueryWrapper<User>().lambda().eq(User::getUsername, criteria.getUsername()));
            if (null == user) {
                map.put("content", new ArrayList<>());
                map.put("totalElements", 0);
                return map;
            }
            queryWrapper.lambda().eq(YxStoreProductReply::getMerId, user.getId());
        }
        queryWrapper.lambda().eq(YxStoreProductReply::getIsDel, 0);
        IPage<YxStoreProductReply> ipage = this.page(new Page<>(pageable.getPageNumber() + 1, pageable.getPageSize()), queryWrapper);
        ipage.getRecords().forEach(yxStoreProductReply -> {
            yxStoreProductReply.setUser(yxUserService.getById(yxStoreProductReply.getUid()));
            ;
            yxStoreProductReply.setStoreProduct(yxStoreProductService.getById(yxStoreProductReply.getProductId()));
            yxStoreProductReply.setSysUser(userService.getById(yxStoreProductReply.getMerId()));
        });
        map.put("content", generator.convert(ipage.getRecords(), YxStoreProductReplyDto.class));
        map.put("totalElements", ipage.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<YxStoreProductReply> queryAll(YxStoreProductReplyQueryCriteria criteria) {
        List<YxStoreProductReply> storeProductReplyList = baseMapper.selectList(QueryHelpPlus.getPredicate(YxStoreProductReply.class, criteria));
        storeProductReplyList.forEach(yxStoreProductReply -> {
            yxStoreProductReply.setUser(yxUserService.getById(yxStoreProductReply.getUid()));
            ;
            yxStoreProductReply.setStoreProduct(yxStoreProductService.getById(yxStoreProductReply.getProductId()));
            yxStoreProductReply.setSysUser(userService.getById(yxStoreProductReply.getMerId()));
        });
        return storeProductReplyList;
    }


    @Override
    public void download(List<YxStoreProductReplyDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (YxStoreProductReplyDto yxStoreProductReply : all) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("用户ID", yxStoreProductReply.getUid());
            map.put("订单ID", yxStoreProductReply.getOid());
            map.put("唯一id", yxStoreProductReply.getUnique());
            map.put("产品id", yxStoreProductReply.getProductId());
            map.put("某种商品类型(普通商品、秒杀商品）", yxStoreProductReply.getReplyType());
            map.put("商品分数", yxStoreProductReply.getProductScore());
            map.put("服务分数", yxStoreProductReply.getServiceScore());
            map.put("评论内容", yxStoreProductReply.getComment());
            map.put("评论图片", yxStoreProductReply.getPics());
            map.put("评论时间", yxStoreProductReply.getAddTime());
            map.put("管理员回复内容", yxStoreProductReply.getMerchantReplyContent());
            map.put("管理员回复时间", yxStoreProductReply.getMerchantReplyTime());
            map.put("0未删除1已删除", yxStoreProductReply.getIsDel());
            map.put("0未回复1已回复", yxStoreProductReply.getIsReply());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
