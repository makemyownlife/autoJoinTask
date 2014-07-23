package com.diyicai.util;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.stereotype.Repository;

@Repository
public class BaseDao extends SqlSessionDaoSupport implements java.io.Serializable {

    public final static Logger log = LogManager.getLogger(BaseDao.class);

}
