package com.sanjinbest.burro.subscriber;

import com.sanjinbest.burro.core.auth.BurroAclHolder;
import com.sanjinbest.burro.core.listener.IChildrenChangedCallback;
import com.sanjinbest.burro.core.listener.INodeChangedCallback;
import com.sanjinbest.burro.core.path.PathTools;
import com.sanjinbest.burro.core.zookeeper.DataNode;
import com.sanjinbest.burro.subscriber.client.BurroConfigSubscriber;
import com.sanjinbest.burro.subscriber.loader.BurroConfigHolder;
import com.sanjinbest.burro.subscriber.loader.BurroConfigLoader;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @Description:
 * @Author: lixin
 * @Date: 2017/12/22 下午4:45
 */
public class SubscriberTest {

    private static BurroConfigSubscriber subscriber;
    private static BurroAclHolder aclHolder;

    public static void main(String[] args) throws Exception {

        BurroConfigLoader loader = new BurroConfigLoader("127.0.0.1:2181","app1","env1","lixin","lixin");
        loader.init();
        loader.load();
        System.out.println(BurroConfigHolder.configHolder);

        Thread.sleep(Integer.MAX_VALUE);


    }
}
