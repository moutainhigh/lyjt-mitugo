/**
 * Copyright (C) 2018-2020
 */
package co.yixiang.modules.user.web.controller;

import co.yixiang.common.api.ApiResult;
import co.yixiang.common.rocketmq.MqProducer;
import co.yixiang.common.web.controller.BaseController;
import co.yixiang.common.web.vo.Paging;
import co.yixiang.constant.MQConstant;
import co.yixiang.constant.ShopConstants;
import co.yixiang.constant.SystemConfigConstants;
import co.yixiang.modules.shop.service.YxSystemConfigService;
import co.yixiang.modules.user.service.YxUserExtractService;
import co.yixiang.modules.user.service.YxUserService;
import co.yixiang.modules.user.web.param.UserExtParam;
import co.yixiang.modules.user.web.param.YxUserExtractQueryParam;
import co.yixiang.modules.user.web.vo.YxUserExtractQueryVo;
import co.yixiang.modules.user.web.vo.YxUserQueryVo;
import co.yixiang.utils.RedisUtil;
import co.yixiang.utils.SecurityUtils;
import co.yixiang.utils.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.hyjf.framework.starter.recketmq.MessageContent;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * <p>
 * 用户提现 前端控制器
 * </p>
 *
 * @author hupeng
 * @since 2019-11-11
 */
@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Api(value = "用户提现", tags = "用户:用户提现", description = "用户提现")
public class UserExtractController extends BaseController {

    @Autowired
    private YxUserExtractService userExtractService;
    @Autowired
    private YxUserService userService;
    @Autowired
    private YxSystemConfigService systemConfigService;
    @Autowired
    private YxUserExtractService yxUserExtractService;

    @Autowired
    private MqProducer mqProducer;

    /**
     * 提现参数
     */
    @GetMapping("/extract/bank")
    @ApiOperation(value = "提现参数", notes = "提现参数")
    public ApiResult<Object> bank() {
        int uid = SecurityUtils.getUserId().intValue();
        YxUserQueryVo userInfo = userService.getYxUserById(uid);
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("commissionCount", userInfo.getNowMoney());
        map.put("phone", StringUtils.isNotBlank(userInfo.getBankMobile()) ? userInfo.getBankMobile() : "");
        map.put("bankNo", StringUtils.isNotBlank(userInfo.getBankNo()) ? userInfo.getBankNo() : "");
        map.put("realName", StringUtils.isNotBlank(userInfo.getRealName()) ? userInfo.getRealName() : "");
        map.put("minPrice", systemConfigService.getData(SystemConfigConstants.USER_EXTRACT_MIN_PRICE));
        map.put("cnapsCode", StringUtils.isNotBlank(userInfo.getCnapsCode()) ? userInfo.getCnapsCode() : "");
        return ApiResult.ok(map);
    }


    /**
     * 用户提现
     */
    @PostMapping("/extract/cash")
    @ApiOperation(value = "用户提现", notes = "用户提现")
    public ApiResult<String> addYxUserExtract(@Valid @RequestBody UserExtParam param) throws Exception {
        int uid = SecurityUtils.getUserId().intValue();
        // 同一时间 用户只能提现一次
        String value = RedisUtil.get(ShopConstants.WITHDRAW_USER_SUBMIT + uid);
        if (StringUtils.isNotBlank(value)) {
            log.info("提现 操作过快，请稍候，id：{}", uid);
            return ApiResult.fail("操作过快，请稍候");
        }

        RedisUtil.set(ShopConstants.WITHDRAW_USER_SUBMIT + uid, 1, 5);
        Integer id = 0;
        try {
            id = userExtractService.updateUserExtract(uid, param);
        } catch (Exception e) {
            log.error("提现 出错，id：{}", uid, e);
            return ApiResult.fail("提现失败，请重试");
        } finally {
            RedisUtil.del(ShopConstants.WITHDRAW_USER_SUBMIT + uid);
        }

        if (id != null && id.intValue() > 0) {
            // 插入一条提现是申请记录后发送mq
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", id + "");
            mqProducer.messageSendDelay(new MessageContent(MQConstant.MITU_TOPIC, MQConstant.MITU_WITHDRAW_TAG, UUID.randomUUID().toString(), jsonObject), 2);
        }
        ApiResult result = ApiResult.ok();
        result.setMsg("申请提现成功，请等待审核");
        return result;
    }


    /**
     * 用户提现表分页列表
     */
    @PostMapping("/getPageList")
    @ApiOperation(value = "获取YxUserExtract分页列表", notes = "用户提现表分页列表", response = YxUserExtractQueryVo.class)
    public ApiResult<Paging<YxUserExtractQueryVo>> getYxUserExtractPageList(@Valid @RequestBody(required = false) YxUserExtractQueryParam yxUserExtractQueryParam) throws Exception {
        int uid = SecurityUtils.getUserId().intValue();
        yxUserExtractQueryParam.setUid(uid);
        // 查询前台用户
        yxUserExtractQueryParam.setUserType(3);
        // 获取提现分页数据
        Paging<YxUserExtractQueryVo> paging = userExtractService.getYxUserExtractPageList(yxUserExtractQueryParam);
        // 获取总累计提现金额
        BigDecimal extractCount = yxUserExtractService.extractSum(uid);
        paging.setSum(extractCount.toString());
        return ApiResult.ok(paging);
    }

}

