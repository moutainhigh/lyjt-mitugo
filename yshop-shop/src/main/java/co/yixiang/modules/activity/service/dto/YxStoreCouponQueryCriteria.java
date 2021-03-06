/**
 * Copyright (C) 2018-2020
 * All rights reserved, Designed By www.yixiang.co
 */
package co.yixiang.modules.activity.service.dto;

import co.yixiang.annotation.Query;
import co.yixiang.modules.shop.service.dto.BaseCriteria;
import lombok.Data;

/**
 * @author hupeng
 * @date 2020-05-13
 */
@Data
public class YxStoreCouponQueryCriteria extends BaseCriteria {

    @Query
    private Integer isDel;
    @Query
    private Integer status;
    private String username;
    private String title;
}
