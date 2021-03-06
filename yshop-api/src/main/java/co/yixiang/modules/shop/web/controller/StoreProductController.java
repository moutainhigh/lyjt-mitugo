/**
 * Copyright (C) 2018-2020
 */
package co.yixiang.modules.shop.web.controller;

import co.yixiang.annotation.AnonymousAccess;
import co.yixiang.common.api.ApiResult;
import co.yixiang.common.constant.CommonConstant;
import co.yixiang.common.web.controller.BaseController;
import co.yixiang.enums.ProductEnum;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.modules.image.service.YxImageInfoService;
import co.yixiang.modules.shop.service.YxStoreInfoService;
import co.yixiang.modules.shop.service.YxStoreProductRelationService;
import co.yixiang.modules.shop.service.YxStoreProductReplyService;
import co.yixiang.modules.shop.service.YxStoreProductService;
import co.yixiang.modules.shop.web.dto.ProductDTO;
import co.yixiang.modules.shop.web.dto.ReplyCountDTO;
import co.yixiang.modules.shop.web.param.YxStoreInfoQueryParam;
import co.yixiang.modules.shop.web.param.YxStoreProductQueryParam;
import co.yixiang.modules.shop.web.param.YxStoreProductRelationQueryParam;
import co.yixiang.modules.shop.web.vo.YxStoreInfoQueryVo;
import co.yixiang.modules.shop.web.vo.YxStoreProductAndStoreQueryVo;
import co.yixiang.modules.shop.web.vo.YxStoreProductQueryVo;
import co.yixiang.modules.shop.web.vo.YxStoreProductReplyQueryVo;
import co.yixiang.utils.SecurityUtils;
import co.yixiang.utils.StringUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商品控制器
 * </p>
 *
 * @author hupeng
 * @since 2019-10-19
 */
@Slf4j
@RestController
@Api(value = "产品模块", tags = "商城:产品模块", description = "产品模块")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StoreProductController extends BaseController {

    private final YxStoreProductService storeProductService;
    private final YxStoreProductRelationService productRelationService;
    private final YxStoreProductReplyService replyService;

    private final YxStoreInfoService yxStoreInfoService;
    private final YxImageInfoService yxImageInfoService;


    /**
     * 获取首页更多产品
     */
    @AnonymousAccess
    @PostMapping("/groom/list")
    @ApiOperation(value = "获取首页更多产品", notes = "获取首页更多产品")
    public ApiResult<Map<String, Object>> moreGoodsList(@RequestBody YxStoreProductQueryParam param) {
        Map<String, Object> map = new LinkedHashMap<>();
        if (param.getType().equals(ProductEnum.TYPE_1.getValue().toString())) {
            // 精品推荐
            map.put("list", storeProductService.getList(param.getPage(), param.getLimit(), 1));
        } else if (param.getType().equals(ProductEnum.TYPE_2.getValue().toString())) {
            // 热门榜单
            map.put("list", storeProductService.getList(param.getPage(), param.getLimit(), 2));
        } else if (param.getType().equals(ProductEnum.TYPE_3.getValue().toString())) {
            // 首发新品
            map.put("list", storeProductService.getList(param.getPage(), param.getLimit(), 3));
        } else if (param.getType().equals(ProductEnum.TYPE_4.getValue().toString())) {
            // 促销单品
            map.put("list", storeProductService.getList(param.getPage(), param.getLimit(), 4));
        }

        return ApiResult.ok(map);
    }

    /**
     * 获取首页更多产品
     */
    @AnonymousAccess
    @GetMapping("/products")
    @ApiOperation(value = "商品列表", notes = "商品列表")
    public ApiResult<List<YxStoreProductQueryVo>> goodsList(YxStoreProductQueryParam productQueryParam) {
        return ApiResult.ok(storeProductService.getGoodsList(productQueryParam));
    }

    /**
     * 为你推荐
     */
    @AnonymousAccess
    @GetMapping("/product/hot")
    @ApiOperation(value = "为你推荐", notes = "为你推荐")
    public ApiResult<List<YxStoreProductQueryVo>> productRecommend(YxStoreProductQueryParam queryParam) {
        return ApiResult.ok(storeProductService.getList(queryParam.getPage().intValue(),
                queryParam.getLimit().intValue(), 1).getRecords());
    }


    /**
     * 商品详情海报
     */
    @GetMapping("/product/poster/{pageType}/{id}")
    @ApiOperation(value = "商品详情海报", notes = "商品详情海报")
    public ApiResult<String> productPoster(@PathVariable String pageType, @PathVariable Integer id) throws IOException, FontFormatException {
        ApiResult result =storeProductService.productPoster(pageType,id);
        return result;
    }

    /**
     * 普通商品详情
     */
    @Log(value = "查看商品详情", type = 1)
    @AnonymousAccess
    @GetMapping("/product/detail/{id}")
    @ApiOperation(value = "普通商品详情", notes = "普通商品详情")
    public ApiResult<ProductDTO> detail(@PathVariable Integer id,
                                        @RequestParam(value = "", required = false) String latitude,
                                        @RequestParam(value = "", required = false) String longitude,
                                        @RequestParam(value = "", required = false) String from) {
//        int uid = SecurityUtils.getUserId().intValue();
        ProductDTO productDTO = storeProductService.goodsDetail(id, 0, 0, latitude, longitude);
        return ApiResult.ok(productDTO);
    }

    /**
     * 添加收藏
     */
    @PostMapping("/collect/add")
    @ApiOperation(value = "添加收藏", notes = "添加收藏")
    public ApiResult<Object> collectAdd(@Validated @RequestBody YxStoreProductRelationQueryParam param) {
        int uid = SecurityUtils.getUserId().intValue();
        productRelationService.addRroductRelation(param, uid, "collect");
        return ApiResult.ok("success");
    }

    /**
     * 取消收藏
     */
    @PostMapping("/collect/del")
    @ApiOperation(value = "取消收藏", notes = "取消收藏")
    public ApiResult<Object> collectDel(@Validated @RequestBody YxStoreProductRelationQueryParam param) {
        int uid = SecurityUtils.getUserId().intValue();
        productRelationService.delRroductRelation(param, uid, "collect");
        return ApiResult.ok("success");
    }

    /**
     * 获取产品评论
     */
    @GetMapping("/reply/list/{id}")
    @ApiOperation(value = "获取产品评论", notes = "获取产品评论")
    public ApiResult<List<YxStoreProductReplyQueryVo>> replyList(@PathVariable Integer id,
                                                                 YxStoreProductQueryParam queryParam) {
        return ApiResult.ok(replyService.getReplyList(id, Integer.valueOf(queryParam.getType()),
                queryParam.getPage().intValue(), queryParam.getLimit().intValue()));
    }

    /**
     * 获取产品评论数据
     */
    @GetMapping("/reply/config/{id}")
    @ApiOperation(value = "获取产品评论数据", notes = "获取产品评论数据")
    public ApiResult<ReplyCountDTO> replyCount(@PathVariable Integer id) {
        return ApiResult.ok(replyService.getReplyCount(id));
    }

    /**
     * 查找产品&店铺信息
     */
    @AnonymousAccess
    @GetMapping("/productsAndStore")
    @ApiOperation(value = "商品列表&店铺信息", notes = "商品列表&店铺信息")
    public ApiResult<YxStoreProductAndStoreQueryVo> productsAndStore(YxStoreProductQueryParam productQueryParam) {
        YxStoreProductAndStoreQueryVo productAndStoreQueryVo = new YxStoreProductAndStoreQueryVo();
        List<YxStoreProductQueryVo> productList = storeProductService.getGoodsList(productQueryParam);
        productAndStoreQueryVo.setProductList(productList);
        //店铺信息
        if (StringUtils.isNotBlank(productQueryParam.getName())) {
            YxStoreInfoQueryParam yxStoreInfoQueryParam = new YxStoreInfoQueryParam();
            yxStoreInfoQueryParam.setStoreName(productQueryParam.getName());
            IPage<YxStoreInfoQueryVo> yxStoreInfoQueryVoList = yxStoreInfoService.getStoreInfoList(yxStoreInfoQueryParam);
            if (!CollectionUtils.isEmpty(yxStoreInfoQueryVoList.getRecords())) {
                productAndStoreQueryVo.setSumStoe(Integer.parseInt(yxStoreInfoQueryVoList.getTotal() + ""));
                productAndStoreQueryVo.setStoreName(yxStoreInfoQueryVoList.getRecords().get(0).getStoreName());
                productAndStoreQueryVo.setIndustryCategoryInfo(yxStoreInfoQueryVoList.getRecords().get(0).getIndustryCategoryInfo());
                productAndStoreQueryVo.setStoreId(yxStoreInfoQueryVoList.getRecords().get(0).getId());
                productAndStoreQueryVo.setStoreAddress(yxStoreInfoQueryVoList.getRecords().get(0).getStoreProvince() + yxStoreInfoQueryVoList.getRecords().get(0).getStoreAddress());
                productAndStoreQueryVo.setStoreImage(yxImageInfoService.selectImgByParam(yxStoreInfoQueryVoList.getRecords().get(0).getId(), CommonConstant.IMG_TYPE_STORE, CommonConstant.IMG_CATEGORY_PIC));

            }
        }
        return ApiResult.ok(productAndStoreQueryVo);
    }


}

