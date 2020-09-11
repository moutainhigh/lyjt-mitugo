/**
 * Copyright (C) 2018-2020
 * All rights reserved, Designed By www.yixiang.co
 */
package co.yixiang.modules.shop.service.impl;

import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.dozer.service.IGenerator;
import co.yixiang.enums.BillDetailEnum;
import co.yixiang.modules.shop.domain.User;
import co.yixiang.modules.shop.domain.YxFundsDetail;
import co.yixiang.modules.shop.domain.YxUserBill;
import co.yixiang.modules.shop.service.UserService;
import co.yixiang.modules.shop.service.YxFundsDetailService;
import co.yixiang.modules.shop.service.YxUserBillService;
import co.yixiang.modules.shop.service.dto.WithdrawReviewQueryCriteria;
import co.yixiang.modules.shop.service.dto.YxUserBillDto;
import co.yixiang.modules.shop.service.dto.YxUserBillQueryCriteria;
import co.yixiang.modules.shop.service.mapper.UserBillMapper;
import co.yixiang.utils.DateUtils;
import co.yixiang.utils.FileUtil;
import co.yixiang.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageInfo;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author hupeng
 * @date 2020-05-12
 */
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "yxUserBill")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class YxUserBillServiceImpl extends BaseServiceImpl<UserBillMapper, YxUserBill> implements YxUserBillService {

    private final IGenerator generator;
    @Autowired
    private UserService userService;
    @Autowired
    private UserBillMapper userBillMapper;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(YxUserBillQueryCriteria criteria, Pageable pageable) {
//        getPage(pageable);
//        PageInfo<YxUserBillDto> page = new PageInfo<>(queryAll(criteria));
        QueryWrapper<YxUserBill> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByDesc(YxUserBill::getAddTime);
        int userRole = 0;
        if (1 == criteria.getUserRole()) {
            userRole = 3;
            queryWrapper.lambda().eq(YxUserBill::getUid, criteria.getUid()).eq(YxUserBill::getUserType, 3);
        }
        if (2 == criteria.getUserRole()) {
            userRole = 2;
            queryWrapper.lambda().eq(YxUserBill::getUid, criteria.getUid()).eq(YxUserBill::getUserType, 2);
        }
        if (StringUtils.isNotBlank(criteria.getUsername())) {
            queryWrapper.lambda().like(YxUserBill::getUsername, criteria.getUsername());
        }
        //收支类型
        if (null != criteria.getPm()) {
            queryWrapper.lambda().eq(YxUserBill::getPm, criteria.getPm());
        }
        if (StringUtils.isNotBlank(criteria.getTitle())) {
            queryWrapper.lambda().like(YxUserBill::getTitle, criteria.getTitle());
        }
        if (StringUtils.isNotBlank(criteria.getAddTimeStart()) && StringUtils.isNotBlank(criteria.getAddTimeEnd())) {
            queryWrapper.lambda().ge(YxUserBill::getAddTime, criteria.getAddTimeStart()).le(YxUserBill::getAddTime, criteria.getAddTimeEnd());
        }
        //明细种类
        if (StringUtils.isNotBlank(criteria.getCategory())) {
            queryWrapper.lambda().eq(YxUserBill::getCategory, criteria.getCategory());
        }

        //明细类型
        if (StringUtils.isNotBlank(criteria.getType())) {
            queryWrapper.lambda().eq(YxUserBill::getType, criteria.getType());
        }
        //日期查找
        if (StringUtils.isNotBlank(criteria.getAddTimeStart()) && StringUtils.isNotBlank(criteria.getAddTimeEnd())) {
            Integer addTimeStart = 0;
            Integer addTimeEnd = 0;
            try {
                Date date = new Date();
                Date dateEnd = new Date();
                SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                date = sf.parse(criteria.getAddTimeStart());// 日期转换为时间戳
                dateEnd = sf.parse(criteria.getAddTimeEnd());// 日期转换为时间戳
                long longDate = date.getTime() / 1000;
                long longDateEnd = dateEnd.getTime() / 1000;
                addTimeStart = (int) longDate;
                addTimeEnd = (int) longDateEnd;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (addTimeEnd != 0 && addTimeStart != 0) {
                queryWrapper.lambda().ge(YxUserBill::getAddTime, addTimeStart).le(YxUserBill::getAddTime, addTimeEnd);
            }
        }
        User user = this.userService.getById(criteria.getUid());

        IPage<YxUserBill> ipage = this.page(new Page<>(pageable.getPageNumber() + 1, pageable.getPageSize()), queryWrapper);

        Map<String, Object> map = new LinkedHashMap<>(4);
        map.put("content", ipage.getRecords());
        map.put("totalElements", ipage.getTotal());
        map.put("totalPrice", user.getTotalAmount());
        BigDecimal bigOut = userBillMapper.getSumPrice(criteria.getUid(), 0, userRole);
        BigDecimal bigIn = userBillMapper.getSumPrice(criteria.getUid(), 1, userRole);
//        map.put("remainPrice", user.getWithdrawalAmount());
        map.put("remainPrice", bigIn);
        map.put("expenditurePrice", bigOut);
        return map;
    }

    @Override
//    @Cacheable
    public List<YxUserBillDto> queryAll(YxUserBillQueryCriteria criteria) {
        Integer startTime = null;
        Integer endTime = null;
        if (StringUtils.isNotBlank(criteria.getAddTimeStart())) {
            startTime = DateUtils.stringToTimestamp(criteria.getAddTimeStart() + " 00:00:00");
            endTime = DateUtils.stringToTimestamp(criteria.getAddTimeEnd() + " 23:59:59");
        }
        return baseMapper.findAllByQueryCriteria(criteria.getUsername(), criteria.getTitle(), criteria.getPm(), startTime, endTime);
    }

    @Override
    public Map<String, Object> queryAll(WithdrawReviewQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<YxUserBillDto> page = new PageInfo<>(queryAll2(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", page.getList());
        map.put("totalElements", page.getTotal());
        return map;
    }

    private List<YxUserBillDto> queryAll2(WithdrawReviewQueryCriteria criteria) {
        return baseMapper.withdrawReviewLog(criteria.getLinkId());
    }

    @Override
    public void download(List<YxUserBillDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (YxUserBillDto yxUserBill : all) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("用户uid", yxUserBill.getUid());
            map.put("关联id", yxUserBill.getLinkId());
            map.put("0 = 支出 1 = 获得", yxUserBill.getPm());
            map.put("账单标题", yxUserBill.getTitle());
            map.put("明细种类", yxUserBill.getCategory());
            map.put("明细类型", yxUserBill.getType());
            map.put("明细数字", yxUserBill.getNumber());
            map.put("剩余", yxUserBill.getBalance());
            map.put("备注", yxUserBill.getMark());
            map.put("添加时间", yxUserBill.getAddTime());
            map.put("0 = 带确定 1 = 有效 -1 = 无效", yxUserBill.getStatus());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    /**
     * 获取用户积分明细
     *
     * @param criteria
     * @param pageable
     * @return
     */
    @Override
    public Map<String, Object> getPointDetail(YxUserBillQueryCriteria criteria, Pageable pageable) {
        BigDecimal totalPoint = BigDecimal.ZERO;
        QueryWrapper<YxUserBill> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(YxUserBill::getCategory, BillDetailEnum.CATEGORY_2);
        if (0 != criteria.getUserRole()) {
            // 非管理员的情况、获取登陆用户的数据
            queryWrapper.lambda().eq(YxUserBill::getUid, criteria.getUid());
            User user = this.userService.getById(criteria.getUid());
            totalPoint = user.getTotalScore();
        } else {

        }
        queryWrapper.lambda().orderByDesc(YxUserBill::getAddTime);
        IPage<YxUserBill> ipage = this.page(new Page<>(pageable.getPageNumber() + 1, pageable.getPageSize()), queryWrapper);

        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", ipage.getRecords());
        map.put("totalElements", ipage.getTotal());
        return map;
    }
}
