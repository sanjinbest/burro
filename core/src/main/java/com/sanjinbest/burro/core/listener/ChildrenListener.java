package com.sanjinbest.burro.core.listener;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description: 子节点或数据监听器
 * @Author: lixin
 * @Date: 2017/12/19 下午3:52
 */
public class ChildrenListener {

    static Logger logger = LoggerFactory.getLogger(ChildrenListener.class);

    /**
     * @Description: 子节点监听
     * @Author: lixin
     * @param: client
     * @param: path
     * @Date: 下午4:24 2017/12/19
     */
    public static void listen(CuratorFramework client, final String path, final IChildrenChangedCallback callback) throws Exception {
        final PathChildrenCache cache = new PathChildrenCache(client,path,true);
        cache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        cache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                switch (event.getType()){
                    case INITIALIZED:
                        logger.info("{} initialized.",path);
                        break;
                    case CHILD_ADDED:
                        logger.info("{} child added,children [{}]",path,client.getChildren());
                        break;
                    case CHILD_UPDATED:
                        logger.info("{} child updated,children [{}]",path,client.getChildren());
                        break;
                    case CHILD_REMOVED:
                        logger.info("{} child removed,children [{}]",path,client.getChildren());
                        break;
                    default:
                        logger.info("{} other event, [{}]",path,event.getType());
                        break;
                }

                if(null != callback){
                    callback.dispose(event);
                }
            }
        });

    }
}
