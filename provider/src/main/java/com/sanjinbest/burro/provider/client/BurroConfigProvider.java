package com.sanjinbest.burro.provider.client;

import com.alibaba.fastjson.JSONObject;
import com.sanjinbest.burro.core.auth.BurroAclHolder;
import com.sanjinbest.burro.core.path.PathTools;
import com.sanjinbest.burro.core.util.Checker;
import com.sanjinbest.burro.core.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @Description: 发布者使用
 * @Author: lixin
 * @Date: 2017/12/20 下午1:51
 */
public class BurroConfigProvider extends ZkConfig{

    Logger logger = LoggerFactory.getLogger(BurroConfigProvider.class);

    //zookeeper client
    protected ZkClient zkClient;

    public BurroConfigProvider(String host){
        super.setHost(host);
        super.addDigestAuth(BurroDefs.Auth.USERNAME,BurroDefs.Auth.PASSWORD);
        super.setNameSpace(BurroDefs.Path.BASE_NAMESPACE);

        buildAndOpen();
    }

    public void buildAndOpen(){
        this.zkClient = ZkClient.getInstance(this);
        this.zkClient.checkAndStart();
    }

    /**
     * @Description: 创建应用
     * @Author: lixin
     * @Date: 下午5:42 2017/12/20
     */
    public boolean createApplication(String application, BurroAclHolder aclHolder){
        zkClient.checkAndStart();
        try{
            return zkClient.createEmptyNode(new ZkNode(PathTools.applicationPath(application),aclHolder.getDefaultAcl()));
        }catch (Exception e){
            logger.error("create application exception [app:{},exc:{}]",application,e.getMessage(),e);
            return false;
        }
    }

    /**
     * @Description: 创建环境
     * @Author: lixin
     * @Date: 下午8:33 2017/12/20
     */
    public boolean createEnv(String application,String env, BurroAclHolder aclHolder){
        zkClient.checkAndStart();
        try{
            return zkClient.createEmptyNode(new ZkNode(PathTools.envPath(application,env), aclHolder.getDefaultAcl()));
        }catch (Exception e){
            logger.error("create env exception [app:{}, env:{}, exc:{}]",application,env,e.getMessage(),e);
            return false;
        }
    }

    /**
     * @Description: 创建配置
     * @Author: lixin
     * @Date: 下午8:38 2017/12/20
     */
    public boolean createProp(String application,String env,String prop, BurroAclHolder aclHolder){
        zkClient.checkAndStart();
        try{
            return zkClient.createEmptyNode(new ZkNode(PathTools.propertiesPath(application,env,prop),aclHolder.getDefaultAcl()));
        }catch (Exception e){
            logger.error("create properties exception [app:{}, env:{}, prop:{}, exc:{}]",application,env,prop,e.getMessage(),e);
            return false;
        }
    }

    public List<String> getApps(BurroAclHolder aclHolder){
        zkClient.checkAndStart();
        try {
            return zkClient.getChildren("/");
        } catch (Exception e) {
            logger.error("get applications exception. [exc:{}]",e.getMessage(),e);
        }
        return null;
    }

    /**
     * @Description: 获取应用下所有环境节点
     * @Author: lixin
     * @Date: 下午3:44 2017/12/21
     */
    public List<String> getEnvs(String application){
        zkClient.checkAndStart();
        try {
            return zkClient.getChildren(PathTools.applicationPath(application));
        } catch (Exception e) {
            logger.error("get application's envs exception. [app:{}, exc:{}]",application,e.getMessage(),e);
        }
        return null;
    }

    /**
     * @Description: 获取应用下某一环境的所有配置节点
     * @Author: lixin
     * @Date: 下午3:44 2017/12/21
     */
    public List<String> getProps(String application,String env){
        zkClient.checkAndStart();
        try {
            return zkClient.getChildren(PathTools.envPath(application,env));
        } catch (Exception e) {
            logger.error("get env's properties exception. [app:{}, env:{}, exc:{}]",application,env,e.getMessage(),e);
        }
        return null;
    }

    /**
     * @Description: 设置配置文件(全量更新)
     * @Author: lixin
     * @Date: 下午1:40 2017/12/21
     */
    public boolean setOneProp(String application, String env, String prop, DataNode data, BurroAclHolder aclHolder){
        if(null == data || null == data.getKV() || data.getKV().size() == 0)return false;
        String propPath = PathTools.propertiesPath(application,env,prop);
        zkClient.checkAndStart();
        try {
            zkClient.setData(PathTools.dataPath(propPath),data.Data2String(),aclHolder.getDefaultAcl());
            return true;
        } catch (Exception e) {
            logger.error("set one properties exception [app:{}, env:{}, prop:{}, data:{}, exc:{}]",application,env,prop,data,e.getMessage(),e);
            return false;
        }
    }

    /**
     * @Description: 设置配置文件(全量更新)，该方法会自动检测节点，如果节点不存在会自动创建
     * @Author: lixin
     * @Date: 下午1:40 2017/12/21
     */
    public boolean createAndSetProp(String application, String env, String prop, DataNode data, BurroAclHolder aclHolder){
        if(null == data || null == data.getKV() || data.getKV().size() == 0)return false;
        String propPath = PathTools.propertiesPath(application,env,prop);
        zkClient.checkAndStart();
        try {
            createAndSet(PathTools.dataPath(propPath),data.Data2String(),aclHolder);
            String sub = data.getSubscribers() != null ? JSONObject.toJSONString(data.getSubscribers()) : "";
            createAndSet(PathTools.subscribersPath(propPath),sub,aclHolder);
            return true;
        } catch (Exception e) {
            logger.error("set one properties exception [app:{}, env:{}, prop:{}, data:{}, exc:{}]",application,env,prop,data,e.getMessage(),e);
            return false;
        }
    }

    /**
     * @Description: 获取一个配置
     * @Author: lixin
     * @Date: 下午3:20 2017/12/21
     */
    public DataNode getOneProp(String application,String env,String prop){
        if(Checker.invalidString(application) || Checker.invalidString(env) || Checker.invalidString(prop)){
            logger.error("invalid param. [app:{}, env:{}, prop:{}]",application,env,prop);
            return null;
        }

        zkClient.checkAndStart();
        try {
            String propPath = PathTools.propertiesPath(application, env, prop);
            DataNode node = new DataNode();
            node.setPath(propPath);
            node.string2Data(zkClient.getData(PathTools.dataPath(propPath)));
            node.string2Subscribers(zkClient.getData(PathTools.subscribersPath(propPath)));
            return node;
        } catch (Exception e) {
            logger.error("get one properties exception [app:{}, env:{}, prop:{}, exc:{}]",application,env,prop,e.getMessage(),e);
        }
        return null;
    }

    /**
     * @Description: 如果节点不存在，创建节点并设置data
     * @Author: lixin
     * @Date: 下午4:08 2017/12/22
     */
    public boolean createAndSet(String path,String data, BurroAclHolder aclHolder) throws Exception {
        if(!exist(path)){
            zkClient.create(new ZkNode(path,data,aclHolder.getDefaultAcl()));
            return exist(path);
        }else{
            return zkClient.setData(path,data,aclHolder.getDefaultAcl()) > 0;
        }
    }

    public boolean exist(String path){
        zkClient.checkAndStart();
        try {
            return zkClient.exist(path);
        } catch (Exception e) {
            logger.error("check path exist exception. [path:{}, exc:{}]",path,e.getMessage(),e);
            return false;
        }
    }

}
