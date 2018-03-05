package com.sanjinbest.burro.provider.db.interfaces;

import com.sanjinbest.burro.provider.db.model.CfgValues;

public interface CfgValuesMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CfgValues record);

    int insertSelective(CfgValues record);

    CfgValues selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CfgValues record);

    int updateByPrimaryKey(CfgValues record);
}