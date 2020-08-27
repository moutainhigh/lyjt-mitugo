/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.coupon.service.mapper;

import co.yixiang.common.mapper.CoreMapper;
import co.yixiang.modules.coupon.domain.YxCouponOrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
* @author liusy
* @date 2020-08-26
*/
@Repository
@Mapper
public interface YxCouponOrderDetailMapper extends CoreMapper<YxCouponOrderDetail> {

}
