package com.sanjinbest.burro.subscriber.loader;

import com.sanjinbest.burro.core.path.PathTools;
import com.sanjinbest.burro.core.util.Checker;
import com.sanjinbest.burro.core.zookeeper.DataNode;
import com.sanjinbest.burro.subscriber.callback.PropertiesChangedCallback;
import com.sanjinbest.burro.subscriber.client.BurroConfigSubscriber;
import com.sanjinbest.burro.subscriber.io.BurroLocalEctype;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: lixin
 * @Date: 2017/12/26 下午5:56
 */
public class BurroConfigLoader {

    Logger logger = LoggerFactory.getLogger(BurroConfigLoader.class);

    //本地副本目录
    private String localPath = "/work/burro";
    //是否启动本地副本，默认为true
    private boolean local = true;

    private String host;
    private String application;
    private String env;
    private String authUser;
    private String authPwd;

    private BurroConfigSubscriber configSubscriber;
    private BurroLocalEctype localEctype;
    private PropertiesChangedCallback callback;

    public BurroConfigLoader(String host,String application, String env, String authUser, String authPwd,String localPath,boolean local) {
        this.host = host;
        this.application = application;
        this.env = env;
        this.authUser = authUser;
        this.authPwd = authPwd;
        this.localPath = Checker.validString(localPath) ? localPath : this.localPath;
        this.local = local;
    }

    public BurroConfigLoader(String host,String application, String env, String authUser, String authPwd) {
        this.host = host;
        this.application = application;
        this.env = env;
        this.authUser = authUser;
        this.authPwd = authPwd;
    }

    public void init(){
        configSubscriber = new BurroConfigSubscriber(host,application,env,authUser,authPwd);
        localEctype = new BurroLocalEctype(localPath);
        callback = new PropertiesChangedCallback(this);

    }

    public void load(){
        List<DataNode> nodeList = loadConfig();
        BurroConfigHolder.configHolder = nodeMap(nodeList);
    }

    //同步配置
    public void syncConfigHolder(String prop){
        if(Checker.invalidString(prop))return;

        DataNode node = configSubscriber.getOneProp(prop);
        //同步到内存
        boolean sync = BurroConfigHolder.syncConfig(node);
        if(sync){
            //同步到本地
            localEctype.writeConfig(application,env,node.getPath(),node);
        }
    }

    /**
     * @Description: 加载配置
     * 1、从zookeeper进行配置加载。
     * 2、如果local=true：加载到新配置，同步到localPath；如未加载到配置，否则从localPath加载配置，如果localPath无配置则退出。
     * 3、如果local=false：未加载到新配置，退出。
     * @Author: lixin
     * @Date: 下午6:02 2017/12/26
     */
    public List<DataNode> loadConfig(){
        //从zk加载配置
        List<DataNode> zkNodes = loadZKConfig();
        //从本地加载
        List<DataNode> localNodes = loadLocalConfig();

        //加载配置失败
        if(Checker.invalidList(zkNodes) && Checker.invalidList(localNodes)){
            logger.error("系统启动加载配置失败。app:{},env:{}",application,env);
            return null;
        }

        //zk读取失败，返回本地副本
        if(Checker.validList(localNodes) && Checker.invalidList(zkNodes)){
            logger.error("系统启动加载配置完成，从本地读取，未读取zk配置，请检查zk启动情况。app:{},env:{}",application,env);
            return localNodes;
        }

        //同步到本地
        checkAndSync(zkNodes,localNodes);

        return zkNodes;
    }

    /**
     * @Description: 检测本地副本，并同步最新配置
     * @Author: lixin
     * @Date: 下午8:45 2017/12/26
     */
    private void checkAndSync(List<DataNode> zkNodes,List<DataNode> localNodes){
        //本地没有副本,直接进行同步到本地
        if(Checker.invalidList(localNodes)){
            for(DataNode node : zkNodes){
                checkAndSync(node,null);
            }
            return;
        }

        Map<String, DataNode> localMap = nodeMap(localNodes);
        //同步本地
        for(DataNode node : zkNodes){
            checkAndSync(node,localMap.get(node.getPath()));
        }
    }

    /**
     * @Description: 加载ZK配置
     * @Author: lixin
     * @Date: 下午6:07 2017/12/26
     */
    private List<DataNode> loadZKConfig(){
        try {
            List<String> props = configSubscriber.getProps();
            if(null == props || props.size() == 0){
                logger.error("zk内没有可读取的配置。app:{},env:{}",application,env);
                return null;
            }

            List<DataNode> nodes = new ArrayList<DataNode>();
            for(String prop : props){
                DataNode oneProp = configSubscriber.getOneProp(prop);
                if(null == oneProp || null == oneProp.getKV()){
                    logger.error("zk内配置文件 {} 为空。app:{},env:{},node:{}",prop,application,env,oneProp);
                    continue;
                }
                configSubscriber.registPropListener(PathTools.common(oneProp.getPath()),callback);
                nodes.add(oneProp);
            }
            return nodes;
        } catch (Exception e) {
            logger.error("加载zk配置异常。app:{},env:{}",application,env,e);
            return null;
        }
    }

    /**
     * @Description: 加载本地副本
     * @Author: lixin
     * @Date: 下午6:07 2017/12/26
     */
    private List<DataNode> loadLocalConfig(){
        if(!local){
            logger.info("loadLocalConfig,同步本地配置开关状态：{}.app:{},env:{},prop:{}",local,application,env);
            return null;
        }
        return localEctype.readAllConfig(application,env);
    }

    /**
     * @Description: 加载一个配置从本地
     * @Author: lixin
     * @Date: 下午8:09 2017/12/26
     */
    private DataNode loadOneLocalConfig(String prop){
        if(!local){
            logger.info("loadOneLocalConfig,同步本地配置开关状态：{}.app:{},env:{},prop:{}",local,application,env,prop);
            return null;
        }
        return localEctype.readConfig(application,env,prop);
    }

    /**
     * @Description: 同步前检测本地配置版本是否早于zk版本
     * @Author: lixin
     * @Date: 下午8:48 2017/12/26
     */
    private boolean checkAndSync(DataNode zkNode,DataNode localNode){
        if(!local){
            logger.info("syncLocalConfig,同步本地配置开关状态：{}.app:{},env:{},prop:{}",local,application,env);
            return false;
        }
        if(null != localNode && Integer.parseInt(localNode.getVersion()) > Integer.parseInt(zkNode.getVersion())){
            logger.error("同步zk配置到本地失败，本地配置版本大于zk配置版本。prop:{},localVersion:{},zkVersion:{}",localNode.getPath(),localNode.getVersion(),zkNode.getVersion());
            return false;
        }

        logger.info("同步zk配置到本地。prop:{},local:{},zk:{}",zkNode.getPath(),zkNode,zkNode);
        return localEctype.writeConfig(application, env, zkNode.getPath(), zkNode);
    }

    private Map<String,DataNode> nodeMap(List<DataNode> nodes){
        Map<String,DataNode> localMap = new HashMap<String, DataNode>();
        if(Checker.invalidList(nodes))return localMap;
        //将list转为map
        for(DataNode node : nodes){
            localMap.put(node.getPath(),node);
        }
        return localMap;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public boolean isLocal() {
        return local;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getAuthUser() {
        return authUser;
    }

    public void setAuthUser(String authUser) {
        this.authUser = authUser;
    }

    public String getAuthPwd() {
        return authPwd;
    }

    public void setAuthPwd(String authPwd) {
        this.authPwd = authPwd;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
