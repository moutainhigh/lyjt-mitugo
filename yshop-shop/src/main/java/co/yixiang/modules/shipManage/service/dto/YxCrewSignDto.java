/**
 * Copyright (C) 2018-2020
 * All rights reserved, Designed By www.yixiang.co
 * 注意：
 * 本软件为www.yixiang.co开发研制，未经购买不得使用
 * 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
 * 一经发现盗用、分享等行为，将追究法律责任，后果自负
 */
package co.yixiang.modules.shipManage.service.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author nxl
 * @date 2020-11-04
 */
@Data
public class YxCrewSignDto implements Serializable {

    /** id */

    private Integer id;


    /** 用户ID */

    private Integer uid;


    /** 用户名 */

    private String username;


    /** 联系电话 */

    private String userPhone;


    /** 体温 */

    private BigDecimal temperature;


    /** 是否删除（0：未删除，1：已删除） */

    private Integer delFlag;


    /** 创建人 */

    private Integer createUserId;


    /** 修改人 */

    private Integer updateUserId;


    /** 创建时间（签到时间） */

    private Timestamp createTime;


    /** 更新时间 */

    private Timestamp updateTime;

    /** 用户名*/
    private String nickName;
}
