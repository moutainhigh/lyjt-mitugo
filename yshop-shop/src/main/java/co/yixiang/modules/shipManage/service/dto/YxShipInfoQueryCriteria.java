/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.shipManage.service.dto;

import co.yixiang.annotation.Query;
import co.yixiang.modules.shop.service.dto.BaseCriteria;
import lombok.Data;

/**
* @author nxl
* @date 2020-11-04
*/
@Data
public class YxShipInfoQueryCriteria extends BaseCriteria {
    /** 船只名称 */

    @Query(type = Query.Type.INNER_LIKE)
    private String shipName;

    /** 船只系列id */
    @Query
    private Integer seriesId;

    /** 船只当前状态：0：在港，1：离港。2：维修中 */
    @Query
    private Integer currentStatus;
    @Query
    private Integer delFlag = 0;
}
