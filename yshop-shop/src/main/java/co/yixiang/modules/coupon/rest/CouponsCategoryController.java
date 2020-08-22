package co.yixiang.modules.coupon.rest;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import co.yixiang.constant.ShopConstants;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.modules.coupon.domain.CouponsCategoryAddRequest;
import co.yixiang.modules.coupon.domain.CouponsCategoryModifyRequest;
import co.yixiang.modules.coupon.domain.CouponsCategoryRequest;
import co.yixiang.modules.coupon.domain.YxCouponsCategory;
import co.yixiang.modules.coupon.service.YxCouponsCategoryService;
import co.yixiang.modules.shop.domain.YxImageInfo;
import co.yixiang.modules.shop.service.YxImageInfoService;
import co.yixiang.utils.SecurityUtils;
import co.yixiang.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author huiy
 * @date 2020-08-14
 */
@AllArgsConstructor
@Api(tags = "卡券分类表管理")
@RestController
@RequestMapping("/api/yxCouponsCategory")
public class CouponsCategoryController {

    private final YxCouponsCategoryService yxCouponsCategoryService;

    private final YxImageInfoService yxImageInfoService;

    @GetMapping
    @Log("查询卡券分类表")
    @ApiOperation("查询卡券分类表")
    @PreAuthorize("@el.check('admin','yxCouponsCategory:list')")
    public ResponseEntity<Object> getYxCouponsCategorys(CouponsCategoryRequest request){
        IPage<YxCouponsCategory> allList = yxCouponsCategoryService.getAllList(request);
        return new ResponseEntity<>(allList, HttpStatus.OK);
    }

    @PostMapping("/add")
    @Log("新增卡券分类表")
    @ApiOperation("新增卡券分类表")
    @PreAuthorize("@el.check('admin','yxCouponsCategory:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody CouponsCategoryAddRequest categoryAddRequest){
        QueryWrapper<YxCouponsCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .and(cateName -> cateName.eq(YxCouponsCategory::getCateName, categoryAddRequest.getCateName()))
                .and(delFlag -> delFlag.eq(YxCouponsCategory::getDelFlag, 0));
        int couponsCategoryCount = yxCouponsCategoryService.count(queryWrapper);
        if (couponsCategoryCount > 0){
            return new ResponseEntity<>("[" +categoryAddRequest.getCateName() + "]分类已存在!", HttpStatus.BAD_REQUEST);
        }

        // 前端未设置值时, 默认初始化值
        if (0 != categoryAddRequest.getIsShow() && 1 != categoryAddRequest.getIsShow()){
            return new ResponseEntity<>("请传入正确的展示状态!", HttpStatus.BAD_REQUEST);
        }

        // 当前登录用户ID
        int loginUserId = SecurityUtils.getUserId().intValue();

        YxCouponsCategory yxCouponsCategory = new YxCouponsCategory();
        BeanUtil.copyProperties(categoryAddRequest, yxCouponsCategory);
        // 只有一级, 无子类.
        yxCouponsCategory.setPid(0);
        // 默认未删除
        yxCouponsCategory.setDelFlag(0);
        yxCouponsCategory.setCreateUserId(loginUserId);
        yxCouponsCategory.setCreateTime(DateTime.now().toTimestamp());
        yxCouponsCategory.setUpdateUserId(loginUserId);
        yxCouponsCategory.setUpdateTime(DateTime.now().toTimestamp());

        // 写入分类数据
        int insCouponCateStatus  = yxCouponsCategoryService.insCouponCate(yxCouponsCategory);
        boolean couponCateStatus = insCouponCateStatus > 0 ? true : false;
        if (couponCateStatus){
            categoryImg(yxCouponsCategory.getId(), categoryAddRequest.getPath(), loginUserId);
        }
        return new ResponseEntity<>(couponCateStatus, HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改卡券分类表")
    @ApiOperation("修改卡券分类表")
    @PreAuthorize("@el.check('admin','yxCouponsCategory:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody CouponsCategoryModifyRequest request){
        QueryWrapper<YxCouponsCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .and(cateName -> cateName.eq(YxCouponsCategory::getCateName, request.getCateName()))
                .and(delFlag -> delFlag.eq(YxCouponsCategory::getDelFlag, 0));
        int couponsCategoryCount = yxCouponsCategoryService.count(queryWrapper);
        if (couponsCategoryCount > 0){
            return new ResponseEntity<>("[" +request.getCateName() + "]分类已存在!", HttpStatus.BAD_REQUEST);
        }

        // 当前登录用户ID
        int loginUserId = SecurityUtils.getUserId().intValue();

        YxCouponsCategory yxCouponsCategory = new YxCouponsCategory();
        BeanUtil.copyProperties(request, yxCouponsCategory);
        // 默认未删除
        yxCouponsCategory.setDelFlag(0);
        yxCouponsCategory.setCreateUserId(SecurityUtils.getUserId().intValue());
        yxCouponsCategory.setCreateTime(DateTime.now().toTimestamp());
        yxCouponsCategory.setUpdateUserId(SecurityUtils.getUserId().intValue());
        yxCouponsCategory.setUpdateTime(DateTime.now().toTimestamp());
        boolean updateStatus = yxCouponsCategoryService.updateById(yxCouponsCategory);

        if (updateStatus){
            categoryImg(request.getId(), request.getPath(), loginUserId);
        }
        return new ResponseEntity<>(updateStatus, HttpStatus.CREATED);
    }

    @Log("删除卡券分类表")
    @ApiOperation("删除卡券分类表")
    @PreAuthorize("@el.check('admin','yxCouponsCategory:del')")
    @DeleteMapping(value = "/delCouponCate/{ids}")
    public ResponseEntity<Object> delete(@PathVariable String ids) {
        String[] idsArr = ids.split(",");
        boolean delStatus = false;
        for (String id : idsArr) {
            YxCouponsCategory yxCouponsCategory = new YxCouponsCategory();
            yxCouponsCategory.setId(Integer.valueOf(id));
            yxCouponsCategory.setDelFlag(1);
            yxCouponsCategory.setUpdateUserId(SecurityUtils.getUserId().intValue());
            yxCouponsCategory.setUpdateTime(DateTime.now().toTimestamp());
            delStatus = yxCouponsCategoryService.removeById(id);
        }
        return new ResponseEntity<>(delStatus, HttpStatus.OK);
    }

    /**
     * 缩略图操作
     * @param typeId
     * @param path   缩略图
     * @param loginUserId
     */
    private void categoryImg(Integer typeId, String path, Integer loginUserId){
        if (StringUtils.isNotBlank(path)) {
            // 查询图片是否存在(已存在则删除)
            QueryWrapper<YxImageInfo> imageInfoQueryWrapper = new QueryWrapper<>();
            imageInfoQueryWrapper.lambda()
                    .and(type -> type.eq(YxImageInfo::getTypeId, typeId))
                    .and(imgCate -> imgCate.eq(YxImageInfo::getImgCategory, ShopConstants.IMG_CATEGORY_PIC))
                    .and(imgType -> imgType.eq(YxImageInfo::getImgType, ShopConstants.IMG_TYPE_COUPONS_CATEGORY))
                    .and(del -> del.eq(YxImageInfo::getDelFlag, false));

            List<YxImageInfo> imageInfoList = yxImageInfoService.list(imageInfoQueryWrapper);

            if (imageInfoList.size() > 0) {
                // 删除已存在的图片
                for (YxImageInfo imageInfo : imageInfoList) {
                    YxImageInfo delImageInfo = new YxImageInfo();
                    delImageInfo.setId(imageInfo.getId());
                    delImageInfo.setDelFlag(1);
                    delImageInfo.setUpdateUserId(loginUserId);
                    delImageInfo.setUpdateTime(DateTime.now().toTimestamp());
                    yxImageInfoService.updateById(delImageInfo);
                }

                // 写入分类对应的图片关联表
                YxImageInfo imageInfo = new YxImageInfo();
                imageInfo.setTypeId(typeId);
                // 卡券分类 img_type 为 5
                imageInfo.setImgType(ShopConstants.IMG_TYPE_COUPONS_CATEGORY);
                imageInfo.setImgCategory(ShopConstants.IMG_CATEGORY_PIC);
                imageInfo.setImgUrl(path);
                imageInfo.setDelFlag(0);
                imageInfo.setCreateUserId(loginUserId);
                imageInfo.setUpdateUserId(loginUserId);
                imageInfo.setCreateTime(DateTime.now().toTimestamp());
                imageInfo.setUpdateTime(DateTime.now().toTimestamp());
                yxImageInfoService.save(imageInfo);
            }
        }
    }
}
