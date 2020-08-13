package co.yixiang.modules.coupons.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import co.yixiang.common.entity.BaseEntity;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * <p>
 * 本地生活, 卡券表
 * </p>
 *
 * @author hupeng
 * @since 2020-08-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="YxCoupons对象", description="本地生活, 卡券表")
public class YxCoupons extends BaseEntity {

    private static final long serialVersionUID = 1L;

@ApiModelProperty(value = "卡券主键")
@TableId(value = "id", type = IdType.AUTO)
private Integer id;

@ApiModelProperty(value = "卡券编号")
private String couponNum;

@ApiModelProperty(value = "卡券名称")
private String couponName;

@ApiModelProperty(value = "卡券类型;1:代金券, 2:折扣券, 3:满减券")
private Integer couponType;

@ApiModelProperty(value = "卡券所属分类")
private Integer couponCategory;

@ApiModelProperty(value = "代金券面额, coupon_type为1时使用")
private BigDecimal denomination;

@ApiModelProperty(value = "折扣券折扣率, coupon_type为2时使用")
private BigDecimal discount;

@ApiModelProperty(value = "使用门槛, coupon_type为3时使用")
private BigDecimal threshold;

@ApiModelProperty(value = "优惠金额, coupon_type为3时使用")
private BigDecimal discountAmount;

@ApiModelProperty(value = "销售价格")
private BigDecimal sellingPrice;

@ApiModelProperty(value = "原价")
private BigDecimal originalPrice;

@ApiModelProperty(value = "平台结算价")
private BigDecimal settlementPrice;

@ApiModelProperty(value = "佣金")
private BigDecimal commission;

@ApiModelProperty(value = "每人限购数量")
private Integer quantityLimit;

@ApiModelProperty(value = "库存")
private Integer inventory;

@ApiModelProperty(value = "销量")
private Integer sales;

@ApiModelProperty(value = "虚拟销量")
private Integer ficti;

@ApiModelProperty(value = "核销次数")
private Integer writeOff;

@ApiModelProperty(value = "有效期始")
private Date expireDateStart;

@ApiModelProperty(value = "有效期止")
private Date expireDateEnd;

@ApiModelProperty(value = "热门优惠; 1:是, 0否")
private Integer isHot;

@ApiModelProperty(value = "状态（0：未上架，1：上架）")
private Integer isShow;

@ApiModelProperty(value = "过期退 0:不支持 1支持")
private Integer outtimeRefund;

@ApiModelProperty(value = "免预约 0:不支持 1支持")
private Integer needOrder;

@ApiModelProperty(value = "随时退 0:不支持 1支持")
private Integer awaysRefund;

@ApiModelProperty(value = "使用条件 描述")
private String useCondition;

@ApiModelProperty(value = "可用时间始")
private String availableTimeStart;

@ApiModelProperty(value = "可用时间止")
private String availableTimeEnd;

@ApiModelProperty(value = "是否删除（0：未删除，1：已删除）")
private Integer delFlag;

@ApiModelProperty(value = "创建人 根据创建人关联店铺")
private Integer createUserId;

@ApiModelProperty(value = "修改人")
private Integer updateUserId;

@ApiModelProperty(value = "创建时间")
private Date createTime;

@ApiModelProperty(value = "更新时间")
private Date updateTime;

@ApiModelProperty(value = "卡券详情")
private String content;

}
