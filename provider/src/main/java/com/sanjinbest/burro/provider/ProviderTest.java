package com.sanjinbest.burro.provider;

import com.sanjinbest.burro.core.auth.BurroAclHolder;
import com.sanjinbest.burro.core.path.PathTools;
import com.sanjinbest.burro.core.zookeeper.BurroDefs;
import com.sanjinbest.burro.core.zookeeper.DataNode;
import com.sanjinbest.burro.provider.client.BurroConfigProvider;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * @Description:
 * @Author: lixin
 * @Date: 2017/12/22 上午11:07
 */
public class ProviderTest {

    private static BurroConfigProvider provider;

    private static BurroAclHolder aclHolder;

    //测试创建应用
    private static void createApp(String appName){
        System.out.println("create app: "+provider.createApplication(appName,aclHolder));
    }

    //测试创建环境
    private static void createEnv(String appName,String envName){
        System.out.println("create env: "+provider.createEnv(appName,envName,aclHolder));
    }

    //测试创建配置
    private static void createProp(String appName,String envName,String propName){
        System.out.println("create prop:"+provider.createProp(appName,envName,propName,aclHolder));
    }

    //测试写入配置
    private static void writeProp(String appName, String envName, String propName, DataNode data){
        System.out.println("write prop:"+provider.createAndSetProp(appName,envName,propName,data,aclHolder));
    }

    //测试读取所有应用
    private static void getApps(){
        System.out.println("apps: "+provider.getApps(aclHolder));
    }

    //测试读取应用下的环境
    private static void getEnvs(String appName){
        System.out.println(appName+": "+provider.getEnvs(appName));
    }

    //测试读取环境下的配置
    private static void getProps(String appName,String env){
        System.out.println(appName+"-"+env+": "+provider.getProps(appName,env));
    }

    //测试读取配置内容
    private static void getProp(String app,String env,String prop){
        System.out.println(app+"-"+env+"-"+prop+": "+provider.getOneProp(app,env,prop));
    }

    //测试读取订阅者
    private static void getSubscribers(String app,String env,String prop){
        System.out.println(app+"-"+env+"-"+prop+": "+provider.getOneProp(app,env,prop).getSubscribers());
    }

    public static void main(String[] args) throws Exception {

        String app = "app1";
        String env1 = "env1";
        String prop1 = "prop1";
        String prop2 = "prop2";

        provider = new BurroConfigProvider("127.0.0.1:2181");
        aclHolder = new BurroAclHolder();
        aclHolder.addAcl(BurroDefs.Auth.USERNAME,BurroDefs.Auth.PASSWORD);
        aclHolder.addAcl("lixin","lixin");
//
//        createApp(app);
//        createEnv(app,env1);
//        createProp(app,env1,prop1);
//        createProp(app,env1,prop2);

        DataNode node = new DataNode();
        node.addKV("k1","v1");
        node.addKV("k2","v3");
        node.setVersion("5");
        Set<String> sub = new HashSet<String>();
        sub.add("127.0.0.2");
        node.setSubscribers(sub);
        writeProp(app,env1,prop1,node);

//        getApps();
//        getEnvs(app);
//        getProps(app,env1);
////        System.out.println(provider.setOneProp(app,env1,prop1,node,aclHolder));
//

    }

}
