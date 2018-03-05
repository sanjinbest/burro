package com.sanjinbest.burro.core.util;

import com.alibaba.fastjson.JSONObject;
import com.sanjinbest.burro.core.zookeeper.ZkNode;
import org.apache.curator.utils.PathUtils;

import java.util.List;

/**
 * @Description:
 * @Author: lixin
 * @Date: 2017/12/19 下午4:55
 */
public class Checker {

    //校验节点
    public static void checkNode(ZkNode zkNode){
        if(null == zkNode || null == zkNode.getPath())throw new IllegalArgumentException("zkNode param is null");
    }

    //校验节点数据
    public static void checkNodeData(ZkNode zkNode){
        checkNode(zkNode);
        if(null == zkNode.getData() || "".equals(zkNode.getData()))throw new IllegalArgumentException("zkNode data is null");
    }

    //校验字符串
    public static boolean invalidString(String str){
        return null == str || "".equals(str);
    }

    public static boolean validString(String str){
        return !invalidString(str);
    }

    //path检查
    public static void pathCheck(String path){
        PathUtils.validatePath(path);
    }

    public static boolean validList(List list){
        return null != list && list.size() > 0;
    }

    public static boolean invalidList(List list){
        return !validList(list);
    }

}
