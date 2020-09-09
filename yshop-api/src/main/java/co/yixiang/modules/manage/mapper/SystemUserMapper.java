package co.yixiang.modules.manage.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import co.yixiang.modules.manage.entity.SystemUser;
import co.yixiang.modules.manage.web.param.SystemUserQueryParam;
import co.yixiang.modules.manage.web.vo.SystemUserQueryVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 系统用户 Mapper 接口
 * </p>
 *
 * @author hupeng
 * @since 2020-08-13
 */
@Repository
public interface SystemUserMapper extends BaseMapper<SystemUser> {

    /**
     * 根据ID获取查询对象
     * @param id
     * @return
     */
    SystemUserQueryVo getUserById(Serializable id);

    /**
     * 获取分页对象
     * @param page
     * @param userQueryParam
     * @return
     */
    IPage<SystemUserQueryVo> getUserPageList(@Param("page") Page page, @Param("param") SystemUserQueryParam userQueryParam);

    /**
     * 商户金额操作
     * @param updateSystemUser
     */
    void updateUserTotal(SystemUser updateSystemUser);

    /**
     * 更新总积分
     * @param merInfo
     * @param oldTotal
     * @return
     */
    @Update("update `user` set total_score=total_score+#{totalScore} where id=#{id} and total_score= #{oldTotal}")
    int updateTotalScore(SystemUser merInfo, BigDecimal oldTotal);
}
