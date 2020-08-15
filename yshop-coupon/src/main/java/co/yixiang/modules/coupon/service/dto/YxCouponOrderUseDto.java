/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.coupon.service.dto;

import lombok.Data;
import java.sql.Timestamp;
import java.io.Serializable;

/**
* @author huiy
* @date 2020-08-14
*/
@Data
public class YxCouponOrderUseDto implements Serializable {

    /** 订单ID */

    private Integer id;


    /** 订单号 */

    private String orderId;


    /** 核销商铺id */

    private Integer storeId;


    /** 店铺名称 */

    private String storeName;


    /** 核销次数 */

    private Integer usedCount;


    /** 是否删除（0：未删除，1：已删除） */

    private Integer delFlag;


    /** 创建人 根据创建人关联店铺 */

    private Integer createUserId;


    /** 修改人 */

    private Integer updateUserId;


    /** 创建时间 */

    private Timestamp createTime;


    /** 更新时间 */

    private Timestamp updateTime;

}
