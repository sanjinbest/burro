package com.sanjinbest.burro.subscriber.callback;

import com.sanjinbest.burro.core.listener.IChildrenChangedCallback;
import com.sanjinbest.burro.core.path.PathTools;
import com.sanjinbest.burro.subscriber.loader.BurroConfigLoader;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description:
 * @Author: lixin
 * @Date: 2017/12/27 下午4:07
 */
public class PropertiesChangedCallback implements IChildrenChangedCallback{
    Logger logger = LoggerFactory.getLogger(PropertiesChangedCallback.class);

    private BurroConfigLoader configLoader;

    public PropertiesChangedCallback(BurroConfigLoader configLoader) {
        this.configLoader = configLoader;
    }

    @Override
    public void dispose(PathChildrenCacheEvent event) {
        PathChildrenCacheEvent.Type type = event.getType();
        if(PathChildrenCacheEvent.Type.CHILD_UPDATED == type){
            String KV = PathTools.childPath(event.getData().getPath());
            if(PathTools.DATA.equals(KV)){
                String prop = PathTools.topPath(event.getData().getPath());
                logger.info("配置文件 {} 修改回调，开始同步",prop);
                configLoader.syncConfigHolder(prop);
            }
        }
    }
}
