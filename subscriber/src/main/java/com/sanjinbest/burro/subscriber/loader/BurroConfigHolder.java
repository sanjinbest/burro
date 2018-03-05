package com.sanjinbest.burro.subscriber.loader;

import com.sanjinbest.burro.core.zookeeper.DataNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @Description:
 * @Author: lixin
 * @Date: 2017/12/27 下午7:38
 */
public class BurroConfigHolder {
    static Logger logger = LoggerFactory.getLogger(BurroConfigLoader.class);

    public static Map<String,DataNode> configHolder;

    public static boolean addConfig(DataNode node){
        if(null != node){
            return configHolder.put(node.getPath(),node) != null;
        }
        return false;
    }

    public static boolean syncConfig(DataNode node){
        if(null == node || !node.valid())return false;

        String path = node.getPath();
        DataNode oldNode = configHolder.get(path);
        if(null == oldNode){//新的配置文件
            logger.info("缓存内无配置，新增配置。{}",path);
            return addConfig(node);
        }else if(Integer.parseInt(node.getVersion()) > Integer.parseInt(oldNode.getVersion())){//版本大于持有的版本
            logger.info("缓存内版本落后于zk，更新配置。{}",path);
            return addConfig(node);
        }else{
            logger.error("缓存内版本提前于zk，不进行更新。oldNode:{}, newNode:{}",oldNode,node);
            return false;
        }
    }
}
