package co.yixiang.modules.shop.rest;

import co.yixiang.logging.aop.log.Log;
import co.yixiang.modules.shop.service.YxUserBillService;
import co.yixiang.modules.shop.service.dto.YxUserBillQueryCriteria;
import co.yixiang.utils.DateUtils;
import co.yixiang.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
* @author hupeng
* @date 2019-11-06
*/
@Api(tags = "商城:用户账单表(资金明细)")
@RestController
@RequestMapping("api")
public class UserBillController {

    private final YxUserBillService yxUserBillService;

    public UserBillController(YxUserBillService yxUserBillService) {
        this.yxUserBillService = yxUserBillService;
    }

    @Log("查询")
    @ApiOperation(value = "查询")
    @GetMapping(value = "/yxUserBill")
    @PreAuthorize("hasAnyRole('admin','YXUSERBILL_ALL','YXUSERBILL_SELECT')")
    public ResponseEntity getYxUserBills(YxUserBillQueryCriteria criteria, Pageable pageable){
        if (StringUtils.isNotBlank(criteria.getAddTimeStart()) && StringUtils.isBlank(criteria.getAddTimeEnd())){
            criteria.setAddTimeEnd(DateUtils.getDate());
        }
        if (StringUtils.isBlank(criteria.getAddTimeStart()) && StringUtils.isNotBlank(criteria.getAddTimeEnd())){
            criteria.setAddTimeStart(DateUtils.getDate());
        }
        return new ResponseEntity(yxUserBillService.queryAll(criteria,pageable), HttpStatus.OK);
    }

}
