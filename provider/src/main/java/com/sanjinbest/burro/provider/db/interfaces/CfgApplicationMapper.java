package com.sanjinbest.burro.provider.db.interfaces;

import com.sanjinbest.burro.provider.db.model.CfgApplication;

public interface CfgApplicationMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CfgApplication record);

    int insertSelective(CfgApplication record);

    CfgApplication selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CfgApplication record);

    int updateByPrimaryKey(CfgApplication record);
}