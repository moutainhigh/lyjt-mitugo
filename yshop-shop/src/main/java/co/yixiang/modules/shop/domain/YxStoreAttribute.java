/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.shop.domain;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import javax.validation.constraints.*;
import java.sql.Timestamp;
import java.io.Serializable;

/**
* @author huiy
* @date 2020-08-14
*/
@Data
@TableName("yx_store_attribute")
public class YxStoreAttribute implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;


    /** 店铺id */
    @NotNull
    private Integer storeId;


    /** 属性值1 */
    @NotBlank
    private String attributeValue1;


    /** 属性值2 */
    @NotBlank
    private String attributeValue2;


    /** 属性类型：0：营业时间，1：店铺服务 */
    @NotNull
    private Integer attributeType;


    /** 是否删除（0：未删除，1：已删除） */
    @NotNull
    @TableField(fill= FieldFill.INSERT_UPDATE)
    private Integer delFlag;


    /** 创建人 */
    private Integer createUserId;


    /** 修改人 */
    private Integer updateUserId;


    /** 创建时间 */
    @NotNull
    @TableField(fill= FieldFill.INSERT)
    private Timestamp createTime;


    /** 更新时间 */
    @NotNull
    @TableField(fill= FieldFill.INSERT_UPDATE)
    private Timestamp updateTime;


    public void copy(YxStoreAttribute source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
