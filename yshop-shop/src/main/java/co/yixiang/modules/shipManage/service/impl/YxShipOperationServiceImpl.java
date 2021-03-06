/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.shipManage.service.impl;

import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.dozer.service.IGenerator;
import co.yixiang.exception.BadRequestException;
import co.yixiang.modules.coupon.domain.YxCouponOrder;
import co.yixiang.modules.coupon.service.mapper.YxCouponOrderMapper;
import co.yixiang.modules.coupon.service.mapper.YxCouponsMapper;
import co.yixiang.modules.shipManage.domain.YxShipOperation;
import co.yixiang.modules.shipManage.domain.YxShipOperationDetail;
import co.yixiang.modules.shipManage.domain.YxShipPassenger;
import co.yixiang.modules.shipManage.param.YxShipOperationInfoResponse;
import co.yixiang.modules.shipManage.param.YxShipOperationResponse;
import co.yixiang.modules.shipManage.param.YxShipPassengerResponse;
import co.yixiang.modules.shipManage.service.YxShipOperationService;
import co.yixiang.modules.shipManage.service.dto.YxShipOperationDto;
import co.yixiang.modules.shipManage.service.dto.YxShipOperationQueryCriteria;
import co.yixiang.modules.shipManage.service.mapper.YxContractTemplateMapper;
import co.yixiang.modules.shipManage.service.mapper.YxShipOperationDetailMapper;
import co.yixiang.modules.shipManage.service.mapper.YxShipOperationMapper;
import co.yixiang.modules.shipManage.service.mapper.YxShipPassengerMapper;
import co.yixiang.utils.*;
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
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// 默认不使用缓存
//import org.springframework.cache.annotation.CacheConfig;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.cache.annotation.Cacheable;

/**
* @author nxl
* @date 2020-11-05
*/
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "yxShipOperation")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class YxShipOperationServiceImpl extends BaseServiceImpl<YxShipOperationMapper, YxShipOperation> implements YxShipOperationService {

    private final IGenerator generator;
    @Autowired
    private YxShipOperationMapper yxShipOperationMapper;
    @Autowired
    private YxShipPassengerMapper yxShipPassengerMapper;
    @Autowired
    private YxShipOperationDetailMapper yxShipOperationDetailMapper;
    @Autowired
    private YxCouponOrderMapper yxCouponOrderMapper;
    @Autowired
    private YxCouponsMapper yxCouponsMapper;

    @Autowired
    private YxContractTemplateMapper yxContractTemplateMapper;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(YxShipOperationQueryCriteria criteria, Pageable pageable) {
       /* getPage(pageable);
        PageInfo<YxShipOperation> page = new PageInfo<>(queryAll(criteria));*/
        QueryWrapper<YxShipOperation> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(YxShipOperation::getDelFlag ,0);
        if(null!=criteria.getShipId()){
            queryWrapper.lambda().eq(YxShipOperation::getShipId ,criteria.getShipId());
        }
        if(null!=criteria.getCaptainName()){
            queryWrapper.lambda().likeRight(YxShipOperation::getCaptainName ,criteria.getCaptainName());
        }
        if(StringUtils.isNotBlank(criteria.getBatchNo())){
            queryWrapper.lambda().eq(YxShipOperation::getBatchNo ,criteria.getBatchNo());
        }
        if(null!=criteria.getStartDate()&&null!=criteria.getEndDate()){
            String startDate = criteria.getStartDate()+" 00:00:00";
            String endDate  = criteria.getEndDate()+" 23:59:59";
            int startInt = DateUtils.stringToTimestamp(startDate);
            int endInt = DateUtils.stringToTimestamp(endDate);
            queryWrapper.lambda().between(YxShipOperation::getLeaveTime ,startInt,endInt);
        }
        IPage<YxShipOperation> ipage = this.page(new Page<>(pageable.getPageNumber() + 1, pageable.getPageSize()), queryWrapper);
        Map<String, Object> map = new LinkedHashMap<>(2);
        List<YxShipOperationDto>yxShipOperationDtoList = generator.convert(ipage.getRecords(), YxShipOperationDto.class);
        for(YxShipOperationDto yxShipOperationDto:yxShipOperationDtoList){
            yxShipOperationDto.setLeaveFortmatTime(DateUtils.timestampToStr10(yxShipOperationDto.getLeaveTime()));
            yxShipOperationDto.setReturnFormatTime(DateUtils.timestampToStr10(yxShipOperationDto.getReturnTime()));
        }
        map.put("content", yxShipOperationDtoList);
        map.put("totalElements", ipage.getTotal());
        return map;
    }
    @Override
    public List<YxShipOperationDto> queryAllNew(YxShipOperationQueryCriteria criteria) {
       /* getPage(pageable);
        PageInfo<YxShipOperation> page = new PageInfo<>(queryAll(criteria));*/
        QueryWrapper<YxShipOperation> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(YxShipOperation::getDelFlag ,0);
        if(null!=criteria.getShipId()){
            queryWrapper.lambda().eq(YxShipOperation::getShipId ,criteria.getShipId());
        }
        if(null!=criteria.getCaptainName()){
            queryWrapper.lambda().likeRight(YxShipOperation::getCaptainName ,criteria.getCaptainName());
        }
        if(StringUtils.isNotBlank(criteria.getBatchNo())){
            queryWrapper.lambda().eq(YxShipOperation::getBatchNo ,criteria.getBatchNo());
        }
        if(null!=criteria.getStartDate()&&null!=criteria.getEndDate()){
            String startDate = criteria.getStartDate()+" 00:00:00";
            String endDate  = criteria.getEndDate()+" 23:59:59";
            int startInt = DateUtils.stringToTimestamp(startDate);
            int endInt = DateUtils.stringToTimestamp(endDate);
            queryWrapper.lambda().between(YxShipOperation::getLeaveTime ,startInt,endInt);
        }
        List<YxShipOperation> shipOperationList = this.list(queryWrapper);
        List<YxShipOperationDto>yxShipOperationDtoList = generator.convert(shipOperationList, YxShipOperationDto.class);
        for(YxShipOperationDto yxShipOperationDto:yxShipOperationDtoList){
            yxShipOperationDto.setLeaveFortmatTime(DateUtils.timestampToStr10(yxShipOperationDto.getLeaveTime()));
            yxShipOperationDto.setReturnFormatTime(DateUtils.timestampToStr10(yxShipOperationDto.getReturnTime()));
        }
        return yxShipOperationDtoList;
    }


    @Override
    //@Cacheable
    public List<YxShipOperation> queryAll(YxShipOperationQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(YxShipOperation.class, criteria));
    }


    @Override
    public void download(List<YxShipOperationDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (YxShipOperationDto yxShipOperation : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("船只出港批次号", yxShipOperation.getBatchNo());
            map.put("船只id", yxShipOperation.getShipId());
            map.put("船只名称", yxShipOperation.getShipName());
            map.put("船长id", yxShipOperation.getCaptainId());
            map.put("船长姓名", yxShipOperation.getCaptainName());
            map.put("承载人数", yxShipOperation.getTotalPassenger());
            map.put("老年人人数", yxShipOperation.getOldPassenger());
            map.put("未成年人数", yxShipOperation.getUnderagePassenger());
            map.put("出港时间", yxShipOperation.getLeaveFortmatTime());
            map.put("回港时间", yxShipOperation.getReturnFormatTime());
            String strStatus = "待出港";
            switch (yxShipOperation.getStatus()){
                case 1:
                    strStatus ="出港";
                    break;
                case 2:
                    strStatus="回港";
                    break;
            }
            map.put("船只状态", strStatus);
           /* map.put("是否删除（0：未删除，1：已删除）", yxShipOperation.getDelFlag());
            map.put("创建人", yxShipOperation.getCreateUserId());
            map.put("修改人", yxShipOperation.getUpdateUserId());
            map.put("创建时间", yxShipOperation.getCreateTime());
            map.put("更新时间", yxShipOperation.getUpdateTime());*/
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    /**
     * 海岸支队大屏（船只出海记录列表）
     * @return
     */
    @Override
    public Map<String, Object> findOperationList(YxShipOperationQueryCriteria criteria,Pageable pageable){
        //
        Map<String, Object> map = new LinkedHashMap<>(2);

        List<YxShipOperationResponse> shipOperationResponseList = new ArrayList<>();
        //查询运营记录信息
        getPage(pageable);
        PageInfo<YxShipOperation> page = new PageInfo<>(queryAll(criteria));
        List<YxShipOperation> shipOperationList = page.getList();
        if(CollectionUtils.isEmpty(shipOperationList)){
            map.put("content", shipOperationResponseList);
            map.put("totalElements", 0);
            return map;
        }
        shipOperationResponseList = CommonsUtils.convertBeanList(shipOperationList,YxShipOperationResponse.class);
        for(YxShipOperationResponse response:shipOperationResponseList){
            //价格
            BigDecimal totlePrice = yxShipOperationMapper.getBatchTotlePrice(response.getBatchNo());
            response.setTotlePricet(totlePrice);
            //船只状态
            String strStatus ="";
            switch (response.getStatus()){
                case 0:
                    strStatus = "待出港";break;
                case 1:
                    strStatus="出港"; break;
                case 2:
                    strStatus="回港";break;
            }
            response.setStatusValue(strStatus);
            //离港时间
            response.setLeaveForTime(DateUtils.timestampToStr10(response.getLeaveTime()));
            //返港时间
            response.setReturnForTime(DateUtils.timestampToStr10(response.getReturnTime()));
            //乘客信息
            QueryWrapper<YxShipPassenger> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(YxShipPassenger::getDelFlag, 0).eq(YxShipPassenger::getBatchNo,response.getBatchNo());
            List<YxShipPassenger> shipPassengerList = yxShipPassengerMapper.selectList(queryWrapper);
            if(!CollectionUtils.isEmpty(shipPassengerList)){
                List<YxShipPassengerResponse> passengerResponseList = CommonsUtils.convertBeanList(shipPassengerList,YxShipPassengerResponse.class);
                response.setListPassenger(passengerResponseList);
            }
        }
        map.put("content", shipOperationResponseList);
        map.put("totalElements", page.getTotal());
        return map;
    }

    /**
     * 获取出行记录详情数据
     * @param optionId
     * @return
     */
    @Override
    public YxShipOperationInfoResponse getOperationDetailInfo(Integer optionId) {
        YxShipOperationInfoResponse response = new YxShipOperationInfoResponse();
        YxShipOperation operation = this.getById(optionId);
        if (null == operation) {
            throw new BadRequestException("根据id：" + optionId + " 获取运营数据错误！");
        }
        //获取详情数据
        QueryWrapper<YxShipOperationDetail> queryWrapperDeatil = new QueryWrapper<>();
        queryWrapperDeatil.lambda().eq(YxShipOperationDetail::getBatchNo, operation.getBatchNo()).eq(YxShipOperationDetail::getDelFlag, 0);

        List<YxShipOperationDetail> shipOperationDetails = yxShipOperationDetailMapper.selectList(queryWrapperDeatil);
        if (CollectionUtils.isEmpty(shipOperationDetails)) {
            throw new BadRequestException("根据批次号：" + operation.getBatchNo() + " 获取运营详情数据错误！");
        }
        BeanUtils.copyProperties(operation, response);
        //离港时间
        response.setLeaveForTime(DateUtils.timestampToStr10(operation.getLeaveTime()));
        //返港时间
        response.setReturnForTime(DateUtils.timestampToStr10(operation.getReturnTime()));

        List<String> orderList = new ArrayList<>();
        for (YxShipOperationDetail detail : shipOperationDetails) {

            YxCouponOrder couponOrder = yxCouponOrderMapper.selectById(detail.getCouponOrderId());
            if (null == couponOrder) {
                throw new BadRequestException("根据id：" + detail.getCouponOrderId() + " 获取卡券订单数据错误！");
            }
            orderList.add(couponOrder.getOrderId());
        }
        response.setOrderList(orderList);
        //乘客信息
        QueryWrapper<YxShipPassenger> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(YxShipPassenger::getDelFlag, 0).eq(YxShipPassenger::getBatchNo, operation.getBatchNo());
        List<YxShipPassenger> shipPassengerList = yxShipPassengerMapper.selectList(queryWrapper);
        if (!CollectionUtils.isEmpty(shipPassengerList)) {
            List<YxShipPassengerResponse> passengerResponseList = CommonsUtils.convertBeanList(shipPassengerList, YxShipPassengerResponse.class);
            response.setListPassenger(passengerResponseList);
        }
        return response;
    }

    /**
     * 打包下载合同
     * @param response
     * @param batchNo
     * @return
     */
    @Override
    public boolean downLoadZipFiles(HttpServletResponse response, String batchNo) {
        String fileName = "批次号_"+batchNo + "_合同下载.zip";
        List<String> nameList = new ArrayList<>();
        QueryWrapper<YxShipOperationDetail> queryWrapperDeatil = new QueryWrapper<>();
        queryWrapperDeatil.lambda().eq(YxShipOperationDetail::getBatchNo, batchNo).eq(YxShipOperationDetail::getDelFlag, 0);

        List<YxShipOperationDetail> shipOperationDetails = yxShipOperationDetailMapper.selectList(queryWrapperDeatil);
        if (CollectionUtils.isEmpty(shipOperationDetails)) {
            throw new BadRequestException("根据批次号：" +batchNo + " 获取运营详情数据错误！");
        }
        //
        /*int tempId = yxCouponsMapper.getTempIdByOrderId(shipOperationDetails.get(0).getCouponOrderId());
        if (0 == tempId) {
            //未配置模板
            throw new BadRequestException("批次号：" +batchNo + " 未配置模板！");
        }
        YxContractTemplate contractTemplate = yxContractTemplateMapper.selectById(tempId);
        //合同模板地址
        contractTemplate.getFilePath();
        FileUtils.downloadZipFiles(response, nameList, fileName);*/
        return false;
    }

}
