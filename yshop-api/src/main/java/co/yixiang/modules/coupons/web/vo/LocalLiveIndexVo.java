package co.yixiang.modules.coupons.web.vo;

import lombok.Data;

import java.util.List;

/**
 * 本地生活 导航实体
 * @Author : huanghui
 */
@Data
public class LocalLiveIndexVo {

    /** 本地生活Banner下导航 */
    List<LocalLifeSliderVo> localLiveMenu;

    /** 本地生活Banner上导航 */
    List<LocalLifeSliderVo> localLiveLink;

    /** 幻灯片 */
    List<LocalLifeSliderVo> sliderList;
}
