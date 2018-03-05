package com.sanjinbest.burro.core.listener;

import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;

/**
 * @Description:
 * @Author: lixin
 * @Date: 2017/12/19 下午4:37
 */
public interface IChildrenChangedCallback {

    void dispose(PathChildrenCacheEvent event);
}
