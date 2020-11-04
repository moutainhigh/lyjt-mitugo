package co.yixiang.modules.coupons.service;

import co.yixiang.modules.coupons.entity.YxCouponsReply;
import co.yixiang.common.service.BaseService;
import co.yixiang.modules.coupons.web.param.YxCouponsReplyQueryParam;
import co.yixiang.modules.coupons.web.vo.YxCouponsReplyQueryVo;
import co.yixiang.common.web.vo.Paging;

import java.io.Serializable;

/**
 * <p>
 * 本地生活评论表 服务类
 * </p>
 *
 * @author lsy
 * @since 2020-11-04
 */
public interface YxCouponsReplyService extends BaseService<YxCouponsReply> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    YxCouponsReplyQueryVo getYxCouponsReplyById(Serializable id) throws Exception;

    /**
     * 获取分页对象
     * @param yxCouponsReplyQueryParam
     * @return
     */
    Paging<YxCouponsReplyQueryVo> getYxCouponsReplyPageList(YxCouponsReplyQueryParam yxCouponsReplyQueryParam) throws Exception;

}
