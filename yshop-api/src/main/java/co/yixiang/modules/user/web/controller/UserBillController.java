/**
 * Copyright (C) 2018-2020
 */
package co.yixiang.modules.user.web.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import co.yixiang.common.api.ApiResult;
import co.yixiang.common.web.controller.BaseController;
import co.yixiang.constant.SystemConfigConstants;
import co.yixiang.enums.AppFromEnum;
import co.yixiang.modules.shop.service.YxSystemConfigService;
import co.yixiang.modules.user.entity.YxSystemAttachment;
import co.yixiang.modules.user.entity.YxUser;
import co.yixiang.modules.user.service.YxSystemAttachmentService;
import co.yixiang.modules.user.service.YxUserBillService;
import co.yixiang.modules.user.service.YxUserExtractService;
import co.yixiang.modules.user.service.YxUserService;
import co.yixiang.modules.user.web.param.PromParam;
import co.yixiang.modules.user.web.param.YxUserBillQueryParam;
import co.yixiang.modules.user.web.vo.YxUserQueryVo;
import co.yixiang.tools.domain.QiniuContent;
import co.yixiang.tools.service.QiNiuService;
import co.yixiang.utils.SecurityUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.validation.Valid;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @ClassName UserBillController
 * @Author hupeng <610796224@qq.com>
 * @Date 2019/11/10
 **/
@Slf4j
@RestController
@Api(value = "用户分销", tags = "用户:用户分销", description = "用户分销")
public class UserBillController extends BaseController {

    @Autowired
    YxUserBillService userBillService;
    @Autowired
    YxUserExtractService extractService;
    @Autowired
    YxSystemConfigService systemConfigService;
    @Autowired
    YxUserService yxUserService;
    @Autowired
    YxSystemAttachmentService systemAttachmentService;

    @Autowired
    QiNiuService qiNiuService;
    
    @Value("${file.path}")
    private String path;


    @Value("${file.localUrl}")
    private String localUrl;


    /**
     * 推广数据    昨天的佣金   累计提现金额  当前佣金
     */
    @GetMapping("/commission")
    @ApiOperation(value = "推广数据", notes = "推广数据")
    public ApiResult<Object> commission() {
        int uid = SecurityUtils.getUserId().intValue();

        //判断分销类型
        String statu = systemConfigService.getData(SystemConfigConstants.STORE_BROKERAGE_STATU);
        YxUserQueryVo userQueryVo = yxUserService.getYxUserById(uid);
        if (StrUtil.isNotEmpty(statu)) {
            if (Integer.valueOf(statu) == 1) {
                if (userQueryVo.getIsPromoter() == 0) {
                    return ApiResult.fail("你不是推广员哦!");
                }
            }
        }

        //昨天的佣金
        BigDecimal lastDayCount = userBillService.yesterdayCommissionSum(uid);
        BigDecimal todayCommission = userBillService.todayCommissionSum(uid);
        //累计提现金额
        BigDecimal extractCount = extractService.extractSum(uid);
        // 累计佣金
        YxUser yxUser = this.yxUserService.getById(uid);

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("lastDayCount", lastDayCount);
        map.put("todayCommission", todayCommission);
        map.put("extractCount", extractCount);
        map.put("commissionCount", yxUser.getBrokeragePrice());

        return ApiResult.ok(map);
    }

    /**
     * 积分记录
     */
    @GetMapping("/integral/list")
    @ApiOperation(value = "积分记录", notes = "积分记录")
    public ApiResult<Object> userInfo(YxUserBillQueryParam queryParam) {
        int uid = SecurityUtils.getUserId().intValue();
        return ApiResult.ok(userBillService.userBillList(uid, queryParam.getPage().intValue(),
                queryParam.getLimit().intValue(), "integral"));
    }

    /**
     * 分销二维码海报生成
     */
    @GetMapping("/spread/banner")
    @ApiOperation(value = "分销二维码海报生成", notes = "分销二维码海报生成")
    public ApiResult<Object> spreadBanner(@RequestParam(value = "", required = false) String form) throws IOException, FontFormatException {
        int uid = SecurityUtils.getUserId().intValue();
        YxUserQueryVo userInfo = yxUserService.getYxUserById(uid);

        String apiUrl = systemConfigService.getData(SystemConfigConstants.API_URL);
        if (StrUtil.isEmpty(apiUrl)) {
            return ApiResult.fail("未配置api地址");
        }

        String spreadUrl;

        String userType = userInfo.getUserType();
        if (!userType.equals(AppFromEnum.ROUNTINE.getValue())) {
            userType = AppFromEnum.H5.getValue();
        }
        String spreadPicName = uid + "_" + userType + "_user_spread.jpg";
        YxSystemAttachment attachmentT = systemAttachmentService.getInfo(spreadPicName);

        if (ObjectUtil.isNotNull(attachmentT)) {
            spreadUrl = attachmentT.getImageType().equals(2)? attachmentT.getSattDir() : apiUrl + "/file/" + attachmentT.getSattDir();
        }else{
            String name = uid + "_" + userType + "_user_wap.jpg";
            YxSystemAttachment attachment = systemAttachmentService.getInfo(name);

            String fileDir = path + "qrcode" + File.separator;
            String qrCodeUrl;
            if (ObjectUtil.isNull(attachment)) {
                String siteUrl = systemConfigService.getData(SystemConfigConstants.SITE_URL);
                if (StrUtil.isEmpty(siteUrl)) {
                    return ApiResult.fail("未配置h5地址");
                }
                //生成二维码
                //判断用户是否小程序,注意小程序二维码生成路径要与H5不一样 不然会导致都跳转到小程序问题
                if (userType.equals(AppFromEnum.ROUNTINE.getValue())) {
                    siteUrl = siteUrl + "/distribution/";
                }
                QrConfig config = new QrConfig(150, 150);
                config.setMargin(0);
                File file = new File(fileDir + name);
                QrCodeUtil.generate(siteUrl + "?spread=" + uid, config,file);
                if (StrUtil.isEmpty(localUrl)) {
                    QiniuContent qiniuContent = qiNiuService.uploadPic(file,qiNiuService.find());
                    systemAttachmentService.attachmentAdd(name, String.valueOf(qiniuContent.getSize()),
                            qiniuContent.getUrl(), qiniuContent.getUrl(),2);
                    qrCodeUrl = qiniuContent.getUrl();
                }else {
                    systemAttachmentService.attachmentAdd(name, String.valueOf(FileUtil.size(file)),
                            fileDir + name, "qrcode/" + name);
                    qrCodeUrl = apiUrl + "/file/qrcode/" + name;
                }

            } else {
                qrCodeUrl = attachment.getImageType().equals(2)?attachment.getSattDir():apiUrl + "/file/" + attachment.getSattDir();
            }

            String spreadPicPath = fileDir + spreadPicName;

                    //创建图片
                    BufferedImage img = new BufferedImage(750, 1624, BufferedImage.TYPE_INT_RGB);
                    //开启画图
                    Graphics2D g = img.createGraphics();
                    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
                    //背景 -- 读取互联网图片
                    InputStream stream = getClass().getClassLoader().getResourceAsStream("background.png");
                    ImageInputStream background = ImageIO.createImageInputStream(stream);
                    BufferedImage back = ImageIO.read(background);

                    g.drawImage(back.getScaledInstance(750, 1624, Image.SCALE_DEFAULT), 0, 0, null); // 绘制缩小后的图

                    BufferedImage head = ImageIO.read(getClass().getClassLoader().getResourceAsStream("head.png"));
                    g.drawImage(head.getScaledInstance(750, 280, Image.SCALE_DEFAULT), 0, 0, null);

                    //商品  banner图
                    //读取互联网图片
                    BufferedImage fx = ImageIO.read(getClass().getClassLoader().getResourceAsStream("fx.jpg"));
                    g.drawImage(fx.getScaledInstance(670, 1000, Image.SCALE_DEFAULT), 40, 280, null);

                    InputStream streamT = getClass().getClassLoader()
                            .getResourceAsStream("Alibaba-PuHuiTi-Regular.otf");
                    File newFileT = new File("Alibaba-PuHuiTi-Regular.otf");
                    FileUtils.copyInputStreamToFile(streamT, newFileT);
                    Font font = Font.createFont(Font.TRUETYPE_FONT, newFileT);

                    //读取二维码图片
                    BufferedImage qrCode = null;
                    try {
                        qrCode = ImageIO.read(new URL(qrCodeUrl));
                    } catch (IOException e) {
                        log.error("二维码图片读取失败", e);
                        e.printStackTrace();
                    }
                    // 绘制缩小后的图
                    g.drawImage(qrCode.getScaledInstance(150, 150, Image.SCALE_DEFAULT), 40, 1320, null);


                    //二维码字体
                    g.setFont(font.deriveFont(Font.PLAIN, 25));
                    g.setColor(new Color(171, 171, 171));
                    //绘制文字
                    g.drawString(userInfo.getNickname() + "邀您加入", 238, 1379);

                    //二维码字体
                    g.setFont(font.deriveFont(Font.PLAIN, 25));
                    g.setColor(new Color(171, 171, 171));
                    //绘制文字
                    g.drawString("扫描或长按小程序码", 210, 1414);

                    g.dispose();
                    //先将画好的海报写到本地
                    File file = new File(spreadPicPath);
                    try {
                        ImageIO.write(img, "jpg", file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (StrUtil.isEmpty(localUrl)) {
                        QiniuContent qiniuContent = qiNiuService.uploadPic(file,qiNiuService.find());
                        systemAttachmentService.attachmentAdd(spreadPicName,String.valueOf(qiniuContent.getSize()),
                                qiniuContent.getUrl(), qiniuContent.getUrl(),2);
                        spreadUrl = qiniuContent.getUrl();
                    }else {
                        systemAttachmentService.attachmentAdd(spreadPicName,String.valueOf(FileUtil.size(new File(spreadPicPath))),
                                spreadPicPath, "qrcode/" + spreadPicName);
                        spreadUrl = apiUrl + "/file/qrcode/" + spreadPicName;
                    }

        }

        java.util.List<Map<String, Object>> list = new ArrayList<>();


        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", 1);
        map.put("pic", "");
        map.put("title", "分享海报");
        map.put("wap_poster", spreadUrl);
        list.add(map);
        return ApiResult.ok(list);
    }

    /**
     * 推荐用户
     * grade == 0  获取一级推荐人
     * grade == 其他  获取二级推荐人
     * keyword 会员名称查询
     * sort  childCount ASC/DESC  团队排序   numberCount ASC/DESC
     * 金额排序  orderCount  ASC/DESC  订单排序
     */
    @PostMapping("/spread/people")
    @ApiOperation(value = "推荐用户", notes = "推荐用户")
    public ApiResult<Object> spreadPeople(@Valid @RequestBody PromParam param) {
        int uid = SecurityUtils.getUserId().intValue();

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("list", yxUserService.getUserSpreadGrade(param, uid));
        map.put("total", yxUserService.getSpreadCount(uid, 1));
        map.put("totalLevel", yxUserService.getSpreadCount(uid, 2));

        return ApiResult.ok(map);
    }

    /**
     * 推广佣金明细
     * type  0 全部  1 消费  2 充值  3 返佣  4 提现
     *
     * @return mixed
     */
    @GetMapping("/spread/commission/{type}")
    @ApiOperation(value = "推广佣金明细", notes = "推广佣金明细")
    public ApiResult<Object> spreadCommission(YxUserBillQueryParam queryParam,
                                              @PathVariable String type) {
        int newType = 0;
        if (NumberUtil.isNumber(type)) {
            newType = Integer.valueOf(type);
        }
        int uid = SecurityUtils.getUserId().intValue();
        return ApiResult.ok(userBillService.getUserBillList(queryParam.getPage().intValue(),
                queryParam.getLimit().intValue(), uid, newType));
    }


    /**
     * 推广订单
     */
    @PostMapping("/spread/order")
    @ApiOperation(value = "推广订单", notes = "推广订单")
    public ApiResult<Object> spreadOrder(@RequestBody String jsonStr) {
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        if (ObjectUtil.isNull(jsonObject.get("page")) || ObjectUtil.isNull(jsonObject.get("limit"))) {
            return ApiResult.fail("参数错误");
        }
        int uid = SecurityUtils.getUserId().intValue();
        Map<String, Object> map = userBillService.spreadOrder(uid, Integer.valueOf(jsonObject.get("page").toString())
                , Integer.valueOf(jsonObject.get("limit").toString()));
        return ApiResult.ok(map);
    }


}
