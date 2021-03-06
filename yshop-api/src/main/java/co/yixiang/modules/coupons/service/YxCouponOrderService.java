package co.yixiang.modules.coupons.service;

import co.yixiang.common.service.BaseService;
import co.yixiang.common.web.vo.Paging;
import co.yixiang.modules.coupons.entity.YxCouponOrder;
import co.yixiang.modules.coupons.web.param.YxCouponOrderQueryParam;
import co.yixiang.modules.coupons.web.vo.*;
import co.yixiang.modules.manage.entity.SystemUser;
import co.yixiang.modules.order.web.dto.ComputeDTO;
import co.yixiang.modules.order.web.dto.CouponCacheDTO;
import co.yixiang.modules.order.web.dto.PriceGroupDTO;
import co.yixiang.modules.order.web.param.OrderParam;
import co.yixiang.modules.order.web.param.RefundParam;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.exception.WxPayException;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 卡券订单表 服务类
 * </p>
 *
 * @author hupeng
 * @since 2020-08-13
 */
public interface YxCouponOrderService extends BaseService<YxCouponOrder> {

    /**
     * 根据ID获取查询对象
     *
     * @param id
     * @return
     */
    YxCouponOrderQueryVo getYxCouponOrderById(Serializable id) throws Exception;

    /**
     * 获取分页对象
     *
     * @param yxCouponOrderQueryParam
     * @return
     */
    Paging<YxCouponOrderQueryVo> getYxCouponOrderPageList(YxCouponOrderQueryParam yxCouponOrderQueryParam) throws Exception;

    PriceGroupDTO getOrderPriceGroup(CouponOrderQueryVo couponOrderQueryVo);

    Double getOrderSumPrice(CouponOrderQueryVo couponOrderQueryVo, String key);

    String cacheOrderInfo(int uid, List<YxCouponsQueryVo> couponsQueryVoList, Integer quantity, PriceGroupDTO priceGroup);

    /**
     * 卡券创建订单
     *
     * @param uid
     * @param key
     * @param param
     * @return
     */
    YxCouponOrder createOrder(int uid, String key, OrderParam param);

    CouponCacheDTO getCacheOrderInfo(int uid, String key);

    /**
     * 删除缓存
     *
     * @param uid
     * @param key
     */
    void delCacheOrderInfo(int uid, String key);

    /**
     * 余额支付
     *
     * @param orderId
     * @param uid
     */
    void yuePay(String orderId, int uid);

    /**
     * 获取订单信息
     *
     * @param unique
     * @param uid
     * @return
     */
    YxCouponOrder getOrderInfo(String unique, int uid);

    void paySuccess(String orderId, String payType);

    /**
     * 计算订单价格
     *
     * @param uid
     * @param key
     * @param couponId
     * @param useIntegral
     * @param shippingType
     * @return
     */
    ComputeDTO computedOrder(int uid, String key, int couponId, int useIntegral, int shippingType);

    /**
     * 通过卡券ID 获取卡券信息和所属公司信息
     *
     * @param couponId
     * @return
     */
    CouponInfoQueryVo getCouponInfo(Integer couponId);

    /**
     * 小程序支付
     *
     * @param orderId
     * @return
     */
    WxPayMpOrderResult wxAppPay(String orderId, String ip) throws WxPayException;

    /**
     * 提交订单退款
     *
     * @param param
     * @param uid
     */
    void orderApplyRefund(RefundParam param, int uid);

    /**
     * 个人中心 我的卡券列表
     *
     * @param yxCouponOrderQueryParam
     * @param uid
     * @return
     */
    List<YxCouponOrderQueryVo> getMyCouponOrderPageList(YxCouponOrderQueryParam yxCouponOrderQueryParam, int uid);

    /**
     * 支付成功处理订单状态
     *
     * @param yxCouponOrder
     */
    void updatePaySuccess(YxCouponOrder yxCouponOrder);

    /**
     * 获取卡券订单详情
     *
     * @param id
     * @param location
     * @return
     */
    YxCouponOrderQueryVo getYxCouponOrderDetail(String id, String location);

    /**
     * 计算卡券订单数量
     *
     * @param uid
     * @return
     */
    OrderCountVO orderData(int uid);

    /**
     * 取消订单
     *
     * @param id
     * @return
     */
    boolean updateOrderStatusCancel(String id);

    /**
     * 卡券核销
     *
     * @param decodeVerifyCode
     * @param uid
     * @return
     */
    Map<String, Object> updateCouponOrder(String decodeVerifyCode, int uid,boolean isAll);

    /**
     * 手动核销卡券
     *
     * @param orderId
     * @param uid
     * @return
     */
    boolean updateCouponOrderInput(String orderId, Integer uid);

    /**
     * 计算用户已购买张数
     *
     * @param uid
     * @param couponId
     * @return
     */
    Integer getBuyCount(int uid, Integer couponId);

    /**
     * 船票核销操作
     * @param decodeVerifyCode
     * @param uid
     * @param shipId
     * @param shipUserId
     * @return
     */
    Map<String, Object> updateShipCouponOrder(String decodeVerifyCode, int uid, Integer shipId, Integer shipUserId, SystemUser user);
}
