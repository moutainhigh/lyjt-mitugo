package co.yixiang.modules.order.web.dto;

import co.yixiang.modules.shop.entity.YxStoreCouponUser;
import co.yixiang.modules.shop.web.vo.YxStoreInfoQueryVo;
import co.yixiang.modules.shop.web.vo.YxStoreStoreCartQueryVo;
import co.yixiang.modules.user.entity.YxUserAddress;
import co.yixiang.modules.user.web.vo.YxUserQueryVo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName ConfirmOrderDTO
 * @Author hupeng <610796224@qq.com>
 * @Date 2019/10/27
 **/
@Data
public class ConfirmNewOrderDTO implements Serializable {
    //地址信息
    private YxUserAddress addressInfo;

    //砍价id
    private Integer bargainId = 0;

    private List<YxStoreStoreCartQueryVo> cartInfo;

    private Integer combinationId = 0;

    //优惠券减
    private Boolean deduction = false;

    private Boolean enableIntegral = true;

    private Double enableIntegralNum = 0d;

    //积分抵扣
    private Integer integralRatio = 0;

    private String orderKey;

    private PriceGroupDTO priceGroup;

    private Integer seckillId = 0;

    private YxStoreCouponUser usableCoupon;

    private YxUserQueryVo userInfo;

    //商铺信息
    private YxStoreInfoQueryVo storeInfo;


}