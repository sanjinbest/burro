package com.sanjinbest.burro.core.listener;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description: 节点监听器,注意：NodeCache会自动创建节点，建议先进行节点创建，然后在增加监听
 * @Author: lixin
 * @Date: 2017/12/19 下午3:55
 */
public class NodeListener{

    static Logger logger = LoggerFactory.getLogger(NodeListener.class);

    /**
     * @Description: 节点监听器
     * @Author: lixin
     * @param: client
     * @param: path
     * @param: callback
     * @Date: 下午4:01 2017/12/19
     */
    public static void listen(CuratorFramework client,String path,final INodeChangedCallback callback) throws Exception {
        final NodeCache cache = new NodeCache(client,path);
        cache.start();
        cache.getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                ChildData data = cache.getCurrentData();
                logger.info("node is changed. [path={}, version={}]",data.getPath(),data.getStat().getVersion());

                if(null != callback){
                    callback.dispose();
                }
            }
        });
    }
}
