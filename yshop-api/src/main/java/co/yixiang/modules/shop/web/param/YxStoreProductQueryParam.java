package co.yixiang.modules.shop.web.param;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import co.yixiang.common.web.param.QueryParam;

/**
 * <p>
 * 商品表 查询参数对象
 * </p>
 *
 * @author nxl
 * @date 2020-08-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="YxStoreProductQueryParam对象", description="商品表查询参数")
public class YxStoreProductQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;

    //类型
    private String type;
    //分类id
    private String sid;
    //新品
    private String news;
    //价格排序
    private String priceOrder;
    //销量
    private String salesOrder;
    //关键字
    private String keyword;
    //名称（店铺名&商品名）
    private String name;
}
