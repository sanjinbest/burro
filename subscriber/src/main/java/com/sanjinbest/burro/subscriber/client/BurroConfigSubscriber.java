package com.sanjinbest.burro.subscriber.client;

import com.alibaba.fastjson.JSONObject;
import com.sanjinbest.burro.core.auth.BurroAclHolder;
import com.sanjinbest.burro.core.exception.InvalidSubscriberException;
import com.sanjinbest.burro.core.listener.IChildrenChangedCallback;
import com.sanjinbest.burro.core.listener.INodeChangedCallback;
import com.sanjinbest.burro.core.path.PathTools;
import com.sanjinbest.burro.core.util.Checker;
import com.sanjinbest.burro.core.zookeeper.DataNode;
import com.sanjinbest.burro.core.zookeeper.ZkClient;
import com.sanjinbest.burro.core.zookeeper.ZkConfig;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Description: 订阅者使用
 * @Author: lixin
 * @Date: 2017/12/20 下午1:51
 */
public class BurroConfigSubscriber extends ZkConfig{
    Logger logger = LoggerFactory.getLogger(BurroConfigSubscriber.class);

    private String application;
    private String env;
    private String authUser;
    private String authPwd;

    //zookeeper client
    private ZkClient zkClient;

    public BurroConfigSubscriber(String host,String application,String env,String authUser,String authPwd){
        super.setHost(host);
        this.application = application;
        this.env = env;
        this.authUser = authUser;
        this.authPwd = authPwd;

        //创建zkClient同时打开链接
        buildAndOpen();
    }

    /**
     * @Description: 创建客户端并打开连接
     * @Author: lixin
     * @Date: 下午5:25 2017/12/26
     */
    public void buildAndOpen(){
        //校验参数
        validSubscriber();
        //设置namespace和权限用户
        super.setNameSpace(PathTools.buildNamespace(application,env));
        super.addDigestAuth(this.authUser,this.authPwd);

        this.zkClient = ZkClient.getInstance(this);
        this.zkClient.checkAndStart();
    }

    /**
     * @Description: 注册单个配置监听(子节点监听类型)
     * @Author: lixin
     * @Date: 下午2:59 2017/12/24
     */
    public boolean registPropListener(String prop, IChildrenChangedCallback callback){
        try{
            return this.zkClient.childrenListen(prop,callback);
        }catch (Exception e){
            logger.error("子节点注册监听失败。path:{}",prop);
            return false;
        }
    }

    /**
     * @Description: 注册环境下所有配置监听（子节点监听类型）
     * @Author: lixin
     * @Date: 下午3:00 2017/12/24
     */
    public boolean registAllListener(IChildrenChangedCallback callback){
        return this.zkClient.childrenListen("/",callback);
    }

    /**
     * @Description: 单个节点监听
     * @Author: lixin
     * @Date: 下午3:02 2017/12/24
     */
    public boolean registOneListener(String path, INodeChangedCallback callback){
        return this.zkClient.nodeListen(path,callback);
    }

    /**
     * @Description: 获取订阅的环境下所有配置节点
     * @Author: lixin
     * @Date: 下午3:44 2017/12/21
     */
    public List<String> getProps() throws Exception{
        zkClient.checkAndStart();
        try {
            return zkClient.getChildren("/");
        } catch (Exception e) {
            logger.error("get env's properties exception. [app:{}, env:{}, exc:{}]",this.application,this.env,e.getMessage(),e);
            throw new Exception(e);
        }
    }

    /**
     * @Description: 获取一个配置
     * @Author: lixin
     * @Date: 下午3:20 2017/12/21
     */
    public DataNode getOneProp(String prop){
        if(Checker.invalidString(prop)){
            logger.error("invalid param.[prop:{}, ip:{}]",prop);
            return null;
        }

        zkClient.checkAndStart();
        try {
            DataNode node = new DataNode();
            node.setPath(prop);
            node.string2Data(zkClient.getData(PathTools.dataPath(prop)));
            node.string2Subscribers(zkClient.getData(PathTools.subscribersPath(prop)));
            return node;
        } catch (Exception e) {
            logger.error("get one properties exception [app:{}, env:{}, prop:{}, exc:{}]",this.application,this.env,prop,e.getMessage(),e);
            return null;
        }
    }

    /**
     * @Description: 设置已订阅该配置
     * @Author: lixin
     * @Date: 下午8:17 2017/12/21
     */
    public boolean setSubscriber(String prop, String ip, BurroAclHolder aclHolder) throws Exception{
        if(Checker.invalidString(prop) || Checker.invalidString(ip)){
            logger.error("invalid param.[prop:{}, ip:{}]",prop,ip);
            return false;
        }

        zkClient.checkAndStart();
        try{
            Stat stat = new Stat();
            String propPath = PathTools.subscribersPath(prop);
            Set<String> subscribers = null;
            String data = zkClient.getData(propPath,stat);
            if(Checker.validString(data)){
                subscribers = JSONObject.parseObject(data,Set.class);
            }
            if(null == subscribers)subscribers = new HashSet<String>();
            boolean isAdd = subscribers.add(ip);
            if(isAdd){
                return zkClient.setData(propPath,JSONObject.toJSONString(subscribers),stat,aclHolder.getDefaultAcl()) > stat.getVersion();
            }
        }catch (Exception e){
            logger.error("set subscriber exception.[app:{}, env:{}, prop:{}, ip:{}, exc:{}]",this.application,this.env,prop,ip,e.getMessage(),e);
        }

        return false;
    }

    public void validSubscriber(){
        if(Checker.invalidString(super.getHost()) || Checker.invalidString(application) || Checker.invalidString(env))throw new InvalidSubscriberException("无效的订阅者");
    }
}
