package co.yixiang.modules.user.service;

import co.yixiang.modules.user.entity.YxLeaveMessage;
import co.yixiang.common.service.BaseService;
import co.yixiang.modules.user.web.dto.YxLeaveMessageGetDataDto;
import co.yixiang.modules.user.web.dto.YxLeaveMessageSaveDto;
import co.yixiang.modules.user.web.param.YxLeaveMessageQueryParam;
import co.yixiang.modules.user.web.vo.YxLeaveMessageQueryVo;
import co.yixiang.common.web.vo.Paging;

import java.io.Serializable;

/**
 * <p>
 * 常用联系人表 服务类
 * </p>
 *
 * @author lsy
 * @since 2020-11-04
 */
public interface YxLeaveMessageService extends BaseService<YxLeaveMessage> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    YxLeaveMessageQueryVo getYxLeaveMessageById(Serializable id) throws Exception;

    /**
     * 获取分页对象
     * @param yxLeaveMessageQueryParam
     * @return
     */
    Paging<YxLeaveMessageQueryVo> getYxLeaveMessagePageList(YxLeaveMessageQueryParam yxLeaveMessageQueryParam) throws Exception;

    /**
     * 用户提交留言(暂时不用)
     *
     * @param request
     * @return
     */
    boolean saveLeaveMessage(YxLeaveMessageSaveDto request);

    /**
     * 根据留言类型获取相关数据
     *
     * @param request
     * @return
     */
    YxLeaveMessageGetDataDto getData(YxLeaveMessageSaveDto request);
}
