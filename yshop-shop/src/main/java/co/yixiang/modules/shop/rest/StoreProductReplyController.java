/**
 * Copyright (C) 2018-2020
 * All rights reserved, Designed By www.yixiang.co
 */
package co.yixiang.modules.shop.rest;

import co.yixiang.exception.BadRequestException;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.modules.shop.domain.YxStoreProductReply;
import co.yixiang.modules.shop.service.YxStoreProductReplyService;
import co.yixiang.modules.shop.service.dto.YxStoreProductReplyQueryCriteria;
import co.yixiang.utils.CurrUser;
import co.yixiang.utils.OrderUtil;
import co.yixiang.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author hupeng
 * @date 2019-11-03
 */
@Api(tags = "商城:评论管理")
@RestController
@RequestMapping("api")
public class StoreProductReplyController {


    private final YxStoreProductReplyService yxStoreProductReplyService;

    public StoreProductReplyController(YxStoreProductReplyService yxStoreProductReplyService) {
        this.yxStoreProductReplyService = yxStoreProductReplyService;
    }

    @Log("查询")
    @ApiOperation(value = "查询")
    @GetMapping(value = "/yxStoreProductReply")
    @PreAuthorize("hasAnyRole('admin','YXSTOREPRODUCTREPLY_ALL','YXSTOREPRODUCTREPLY_SELECT')")
    public ResponseEntity getYxStoreProductReplys(YxStoreProductReplyQueryCriteria criteria, Pageable pageable) {
        CurrUser currUser = SecurityUtils.getCurrUser();
        criteria.setUserRole(currUser.getUserRole());
        if (null != currUser.getChildUser()) {
            criteria.setChildUser(currUser.getChildUser());
        }
        return new ResponseEntity(yxStoreProductReplyService.queryAll(criteria, pageable), HttpStatus.OK);
    }


    @Log("修改")
    @ApiOperation(value = "修改")
    @PutMapping(value = "/yxStoreProductReply")
    @PreAuthorize("hasAnyRole('admin','YXSTOREPRODUCTREPLY_ALL','YXSTOREPRODUCTREPLY_EDIT')")
    public ResponseEntity update(@Validated @RequestBody YxStoreProductReply resources) {
        yxStoreProductReplyService.save(resources);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @Log("评论回复")
    @ApiOperation(value = "评论回复")
    @PutMapping(value = "/yxStoreProductReply/reply")
    @PreAuthorize("hasAnyRole('admin','YXSTOREPRODUCTREPLY_ALL','YXSTOREPRODUCTREPLY_EDIT')")
    public ResponseEntity updateReply(@Validated @RequestBody YxStoreProductReply resources) {
        if (null == resources.getId()) {
            throw new BadRequestException("主键id缺失");
        }
        YxStoreProductReply update = new YxStoreProductReply();
        update.setId(resources.getId());
        update.setIsReply(1);
        update.setMerchantReplyContent(resources.getMerchantReplyContent());
        update.setMerchantReplyTime(OrderUtil.getSecondTimestampTwo());
        yxStoreProductReplyService.updateById(update);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @Log("删除")
    @ApiOperation(value = "删除")
    @DeleteMapping(value = "/yxStoreProductReply/{id}")
    @PreAuthorize("hasAnyRole('admin','YXSTOREPRODUCTREPLY_ALL','YXSTOREPRODUCTREPLY_DELETE')")
    public ResponseEntity delete(@PathVariable Integer id) {
        //if(StrUtil.isNotEmpty("22")) throw new BadRequestException("演示环境禁止操作");
        YxStoreProductReply reply = new YxStoreProductReply();
        reply.setIsDel(1);
        reply.setId(id);
        yxStoreProductReplyService.saveOrUpdate(reply);
        return new ResponseEntity(HttpStatus.OK);
    }
}
