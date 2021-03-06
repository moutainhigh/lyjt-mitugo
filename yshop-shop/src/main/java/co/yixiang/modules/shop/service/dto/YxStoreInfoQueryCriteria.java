/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.shop.service.dto;

import lombok.Data;
import java.util.List;
import co.yixiang.annotation.Query;

import javax.validation.constraints.NotBlank;

/**
* @author nxl
* @date 2020-08-14
*/
@Data
public class YxStoreInfoQueryCriteria extends BaseCriteria{
    /** 店铺名称 */
    @Query(type = Query.Type.INNER_LIKE)
    private String storeName;

    /** 管理人电话 */
    @Query(type = Query.Type.EQUAL)
    private String manageMobile;

    @Query(type = Query.Type.EQUAL)
    private Integer status;

    @Query
    private Integer delFlag;

    @Query
    private Integer merId;

}
