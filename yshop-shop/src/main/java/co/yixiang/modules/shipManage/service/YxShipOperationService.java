/**
* Copyright (C) 2018-2020
* All rights reserved, Designed By www.yixiang.co
* 注意：
* 本软件为www.yixiang.co开发研制，未经购买不得使用
* 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
* 一经发现盗用、分享等行为，将追究法律责任，后果自负
*/
package co.yixiang.modules.shipManage.service;
import co.yixiang.common.service.BaseService;
import co.yixiang.modules.shipManage.domain.YxShipOperation;
import co.yixiang.modules.shipManage.param.YxShipOperationInfoResponse;
import co.yixiang.modules.shipManage.service.dto.YxShipOperationDto;
import co.yixiang.modules.shipManage.service.dto.YxShipOperationQueryCriteria;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
* @author nxl
* @date 2020-11-05
*/
public interface YxShipOperationService  extends BaseService<YxShipOperation>{

/**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(YxShipOperationQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<YxShipOperationDto>
    */
    List<YxShipOperation> queryAll(YxShipOperationQueryCriteria criteria);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<YxShipOperationDto> all, HttpServletResponse response) throws IOException;

    /**
     * 海岸支队大屏（船只出海记录列表）
     * @return
     */
    Map<String, Object> findOperationList(YxShipOperationQueryCriteria criteria,Pageable pageable);

    /**
     * 获取出行记录详情数据
     * @param optionId
     * @return
     */
    YxShipOperationInfoResponse getOperationDetailInfo(Integer optionId);

    /**
     * 打包下载合同
     * @param response
     * @param batchNo
     * @return
     */
    boolean downLoadZipFiles(HttpServletResponse response, String batchNo);

    List<YxShipOperationDto> queryAllNew(YxShipOperationQueryCriteria criteria);
}
