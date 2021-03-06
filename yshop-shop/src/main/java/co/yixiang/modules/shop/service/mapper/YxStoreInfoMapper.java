/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.shop.service.mapper;

import co.yixiang.common.mapper.CoreMapper;
import co.yixiang.modules.mybatis.GeoBaseInsertMapper;
import co.yixiang.modules.mybatis.GeoBaseUpdateMapper;
import co.yixiang.modules.shop.domain.YxStoreInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.base.BaseSelectMapper;

/**
* @author nxl
* @date 2020-08-14
*/
@Repository
@Mapper
public interface YxStoreInfoMapper extends GeoBaseInsertMapper<YxStoreInfo>, GeoBaseUpdateMapper<YxStoreInfo>, CoreMapper<YxStoreInfo> {
    @Update("update yx_store_info set status = #{status} where id = #{id}")
    void updateOnsale(@Param("status")int status, @Param("id")Integer id);

    @Update("update yx_store_info set del_flag = 1 where id = #{id}")
    void updateDelFlg(@Param("id")Integer id);
}
