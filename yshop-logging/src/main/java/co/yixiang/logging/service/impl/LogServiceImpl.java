/**
 * Copyright (C) 2018-2020
 * All rights reserved, Designed By www.yixiang.co
 */
package co.yixiang.logging.service.impl;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.dozer.service.IGenerator;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.logging.service.LogService;
import co.yixiang.logging.service.dto.LogErrorDTO;
import co.yixiang.logging.service.dto.LogQueryCriteria;
import co.yixiang.logging.service.dto.LogSmallDTO;
import co.yixiang.logging.service.mapper.LogMapper;
import co.yixiang.utils.FileUtil;
import co.yixiang.utils.StringUtils;
import co.yixiang.utils.ValidationUtil;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author hupeng
 * @date 2018-11-24
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class LogServiceImpl extends BaseServiceImpl<LogMapper, co.yixiang.logging.domain.Log> implements LogService {
    private final org.apache.commons.logging.Log logger = LogFactory.getLog(LogServiceImpl.class);

    private final LogMapper logMapper;

    private final IGenerator generator;

    String couponMethod = "co.yixiang.modules.coupon.rest.YxCouponsController.getYxCouponss" ;
    String goodMethod = "co.yixiang.modules.shop.web.controller.StoreProductController.detail" ;


    public LogServiceImpl(LogMapper logMapper, IGenerator generator) {
        this.logMapper = logMapper;
        this.generator = generator;
    }

    @Override
    public Object findAllByPageable(String nickname, Pageable pageable) {
        getPage(pageable);
        PageInfo<co.yixiang.logging.domain.Log> page = new PageInfo<>(logMapper.findAllByPageable(nickname));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content" , page.getList());
        map.put("totalElements" , page.getTotal());
        return map;
    }


    @Override
    public Object queryAll(LogQueryCriteria criteria, Pageable pageable) {

        getPage(pageable);
        PageInfo<co.yixiang.logging.domain.Log> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        String status = "ERROR" ;
        if (status.equals(criteria.getLogType())) {
            map.put("content" , generator.convert(page.getList(), LogErrorDTO.class));
            map.put("totalElements" , page.getTotal());
        }
        map.put("content" , page.getList());
        map.put("totalElements" , page.getTotal());
        return map;
    }

    @Override
    public List<co.yixiang.logging.domain.Log> queryAll(LogQueryCriteria criteria) {
        return baseMapper.selectList(QueryHelpPlus.getPredicate(co.yixiang.logging.domain.Log.class, criteria));
    }

    @Override
    public Object queryAllByUser(LogQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<co.yixiang.logging.domain.Log> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content" , generator.convert(page.getList(), LogSmallDTO.class));
        map.put("totalElements" , page.getTotal());
        return map;

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(String username, String ip, ProceedingJoinPoint joinPoint,
                     co.yixiang.logging.domain.Log log, Long uid) {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Log aopLog = method.getAnnotation(Log.class);

        // 方法路径
        String methodName = joinPoint.getTarget().getClass().getName() + "." + signature.getName() + "()" ;

        if (methodName.contains(goodMethod) || methodName.contains(couponMethod)) {
            Integer id = getId(joinPoint);
            if (id == 0) {
                logger.info("访问方法为:" + methodName);
            }
            log.setProductType(1);
            log.setProductId(id);
            if (couponMethod.equals(methodName)) {
                log.setProductType(2);
            }
        }
        StringBuilder params = new StringBuilder("{");
        //参数值
        Object[] argValues = joinPoint.getArgs();
        //参数名称
        String[] argNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
        String ignores = StringUtils.isEmpty(aopLog.ignore())?"":aopLog.ignore();
        String[] ignoreArgs = ignores.split(";");
        Map<String,String> ignoreMap = new HashMap<>();
        for (String ignore : ignoreArgs) {
            if(StringUtils.isNotBlank(ignore)){
                String[] key = ignore.split(":");
                if(key.length>=2){
                    ignoreMap.put(key[0],key[1]);
                }
            }

        }
        if (argValues != null && argValues.length > 0) {
            for (int i = 0; i < argValues.length; i++) {
                String value = String.valueOf(argValues[i]);
                if(ignoreMap.containsKey(argNames[i])){
//                    value = getValue( String.valueOf(argValues[i]),ignoreMap.get(argNames[i]));
                }
                params.append(" ").append(argNames[i]).append(": ").append(value);

            }
        }
        // 描述
        if (log != null) {
            log.setDescription(aopLog.value());
        }
        //类型 0-后台 1-前台
        log.setType(aopLog.type());
        if (uid != null) {
            log.setUid(uid);
        }
        assert log != null;
        log.setRequestIp(ip);

        String loginPath = "login" ;
        if (loginPath.equals(signature.getName())) {
            try {
                assert argValues != null;
                username = new JSONObject(argValues[0]).get("username").toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        log.setAddress(StringUtils.getCityInfo(log.getRequestIp()));
        log.setMethod(methodName);
        log.setUsername(username);
        log.setParams(params.toString() + " }");
        this.save(log);
    }

    @Override
    public Object findByErrDetail(Long id) {
        co.yixiang.logging.domain.Log log = this.getById(id);
        ValidationUtil.isNull(log.getId(), "Log" , "id" , id);
        byte[] details = log.getExceptionDetail();
        return Dict.create().set("exception" , new String(ObjectUtil.isNotNull(details) ? details : "".getBytes()));
    }

    @Override
    public void download(List<co.yixiang.logging.domain.Log> logs, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (co.yixiang.logging.domain.Log log : logs) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("用户名" , log.getUsername());
            map.put("IP" , log.getRequestIp());
            map.put("IP来源" , log.getAddress());
            map.put("描述" , log.getDescription());
            map.put("浏览器" , log.getBrowser());
            map.put("请求耗时/毫秒" , log.getTime());
            map.put("异常详情" , new String(ObjectUtil.isNotNull(log.getExceptionDetail()) ? log.getExceptionDetail() : "".getBytes()));
            map.put("创建日期" , log.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delAllByError() {
        logMapper.deleteByLogType("ERROR");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delAllByInfo() {
        logMapper.deleteByLogType("INFO");
    }

    public Integer getId(ProceedingJoinPoint joinPoint) {
        String value = "0" ;
        Object[] args = joinPoint.getArgs();
        String[] paramNames = ((CodeSignature) joinPoint.getSignature()).getParameterNames();
        // 遍历请求中的参数名
        for (String reqParam : paramNames) {
            if (reqParam.equals("id")) {
                int index = ArrayUtil.indexOf(paramNames, reqParam);
                if (index != -1) {
                    value = String.valueOf(args[index]);
                }
            }
        }
        return Integer.parseInt(value);
    }

    public String getValue(String param,String key){
        com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(param);
        List<String> keyList = Arrays.asList(key.split(","));
        for (String name : keyList) {
            jsonObject.remove(name);
        }
        return String.valueOf(jsonObject);
    }
}
