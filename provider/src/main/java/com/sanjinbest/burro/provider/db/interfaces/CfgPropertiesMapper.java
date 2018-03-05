package com.sanjinbest.burro.provider.db.interfaces;

import com.sanjinbest.burro.provider.db.model.CfgProperties;

public interface CfgPropertiesMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CfgProperties record);

    int insertSelective(CfgProperties record);

    CfgProperties selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CfgProperties record);

    int updateByPrimaryKey(CfgProperties record);
}