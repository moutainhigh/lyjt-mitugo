package co.yixiang.modules.user.web.param;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import co.yixiang.common.web.param.QueryParam;

/**
 * <p>
 * 商家入驻表 查询参数对象
 * </p>
 *
 * @author lsy
 * @date 2020-11-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="YxMerchantsSettlementQueryParam对象", description="商家入驻表查询参数")
public class YxMerchantsSettlementQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;
}
