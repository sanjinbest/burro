package com.sanjinbest.burro.core.path;

import com.sanjinbest.burro.core.util.Checker;
import com.sanjinbest.burro.core.zookeeper.BurroDefs;

/**
 * @Description:
 * @Author: lixin
 * @Date: 2017/12/19 下午1:01
 */
public class PathTools {

    public static final String DATA = "data";

    //配置文件的KV节点
    public static final String KV = "KV";
    //配置文件的版本号节点
    public static final String VERSION = "version";
    //配置文件的订阅者
    public static final String SUBSCRIBERS = "subscribers";

    private static final String SPLIT = "/";

    public static String applicationPath(String application){
        if(Checker.invalidString(application))throw new IllegalArgumentException("application name not null!");
        return joint(application);
    }

    public static String envPath(String application,String env){
        if(Checker.invalidString(env))throw new IllegalArgumentException("application name not null!");
        return joint(application,env);
    }

    public static String propertiesPath(String application,String env,String properties){
        if(Checker.invalidString(properties))throw new IllegalArgumentException("application name not null!");
        return joint(application,env,properties);
    }

    public static String dataPath(String propPath){
        if(Checker.invalidString(propPath))throw new IllegalArgumentException("propPath name not null!");
        return joint(propPath,DATA);
    }

    public static String versionPath(String propPath){
        if(Checker.invalidString(propPath))throw new IllegalArgumentException("propPath name not null!");
        return joint(propPath,VERSION);
    }

    public static String subscribersPath(String propPath){
        if(Checker.invalidString(propPath))throw new IllegalArgumentException("propPath name not null!");
        return joint(propPath,SUBSCRIBERS);
    }

    public static String common(String ... strs){
        if(null == strs || strs.length == 0)throw new IllegalArgumentException("strs name not null!");
        return joint(strs);
    }

    public static String topPath(String str){
        if(Checker.invalidString(str))return null;
        String[] strArr = str.split(SPLIT);
        if(null == strArr || strArr.length == 0 || strArr.length < 2)return null;

        return strArr[1];
    }

    public static String childPath(String str){
        if(Checker.invalidString(str))return null;
        String[] strArr = str.split(SPLIT);
        if(null == strArr || strArr.length == 0 || strArr.length < 3)return null;

        return strArr[2];
    }

    public static String buildNamespace(String app,String env){
        if(Checker.invalidString(app) && Checker.invalidString(env))return BurroDefs.Path.BASE_NAMESPACE;
        return BurroDefs.Path.BASE_NAMESPACE + joint(app,env);
    }

    private static String joint(String ... strs){
        StringBuilder sb = new StringBuilder();
        for(String str : strs){
            if(Checker.invalidString(str))continue;
            if(str.indexOf(SPLIT) < 0){
                sb.append(SPLIT);
            }
            sb.append(str);
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        String str = "/prop/KV";
        System.out.println(topPath(str));
    }

}
