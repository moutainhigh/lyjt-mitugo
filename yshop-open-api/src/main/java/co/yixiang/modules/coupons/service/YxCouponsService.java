package co.yixiang.modules.coupons.service;

import co.yixiang.common.api.ApiResult;
import co.yixiang.modules.coupons.entity.YxCoupons;
import co.yixiang.common.service.BaseService;
import co.yixiang.modules.coupons.web.param.YxCouponsQueryParam;
import co.yixiang.modules.coupons.web.vo.YxCouponsQueryVo;
import co.yixiang.common.web.vo.Paging;

import java.io.Serializable;

/**
 * <p>
 * 本地生活, 卡券表 服务类
 * </p>
 *
 * @author zqq
 * @since 2020-12-11
 */
public interface YxCouponsService extends BaseService<YxCoupons> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    YxCouponsQueryVo getYxCouponsById(Serializable id) throws Exception;

    /**
     * 获取分页对象
     * @param yxCouponsQueryParam
     * @return
     */
    Paging<YxCouponsQueryVo> getYxCouponsPageList(YxCouponsQueryParam yxCouponsQueryParam) throws Exception;

    /**
     * 查询卡券列表
     * @param yxCouponsQueryParam
     * @return
     */
    ApiResult selectYxCouponsPageList(YxCouponsQueryParam yxCouponsQueryParam) throws Exception;
}