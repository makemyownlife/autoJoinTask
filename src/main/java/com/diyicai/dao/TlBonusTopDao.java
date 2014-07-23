package com.diyicai.dao;

import com.diyicai.util.BaseDao;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: zhangyong
 * Date: 13-12-30
 * Time: 下午5:43
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class TlBonusTopDao extends BaseDao {

    public Integer getCountTlBonusTopByRange(int range) {
        return (Integer) this.getSqlSession().selectOne("TlBonusTop.getCountTlBonusTop", range);
    }

    public Map getRangeInfo(int range) {
        return (Map) this.getSqlSession().selectOne("TlBonusTop.getRangeInfo", range);
    }

    public List getTlBonusTopList(Map param) {
        long startTime = System.currentTimeMillis();
        List list = this.getSqlSession().selectList("TlBonusTop.getTlBonusTopList", param);
        logger.info("耗时：" + (System.currentTimeMillis() - startTime));
        return list;
    }

}
