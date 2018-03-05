package com.sanjinbest.burro.provider.db.interfaces;

import com.sanjinbest.burro.provider.db.model.CfgEnv;

public interface CfgEnvMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CfgEnv record);

    int insertSelective(CfgEnv record);

    CfgEnv selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CfgEnv record);

    int updateByPrimaryKey(CfgEnv record);
}