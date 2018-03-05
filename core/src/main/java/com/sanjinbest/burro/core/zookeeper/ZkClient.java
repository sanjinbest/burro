package com.sanjinbest.burro.core.zookeeper;

import com.sanjinbest.burro.core.listener.ChildrenListener;
import com.sanjinbest.burro.core.listener.IChildrenChangedCallback;
import com.sanjinbest.burro.core.listener.INodeChangedCallback;
import com.sanjinbest.burro.core.listener.NodeListener;
import com.sanjinbest.burro.core.util.Checker;
import org.apache.curator.framework.AuthInfo;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @Description: zookeeper 实例，提供基础zookeeper操作
 * @Author: lixin
 * @Date: 2017/12/19 上午11:57
 */
public class ZkClient {

    Logger logger = LoggerFactory.getLogger(ZkClient.class);

    //真正的zookeeper操作者
    protected CuratorFramework handler = null;

    public static ZkClient getInstance(ZkConfig config){
        return ZkClientCreator.create(config);
    }

    /**
     * @Description: 创建client
     * @Author: lixin
     * @Date: 下午2:49 2017/12/19
     */
    private static class ZkClientCreator{
        private static ZkClient instance = new ZkClient();
        private static ZkClient create(ZkConfig config){
            config.valid();
            CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder();
            List<AuthInfo> authInfos = config.getAuthInfos();
            if(null != authInfos && authInfos.size() > 0){
                builder.authorization(authInfos);
            }

            instance.handler = builder.connectString(config.getHost())
                    .connectionTimeoutMs(config.getConnectionTimeout())
                    .sessionTimeoutMs(config.getSessionTimeout())
                    .retryPolicy(config.getRetryPolicy())
                    .namespace(config.getNameSpace())
                    .build();

            return instance;
        }
    }

    /**
     * @Description: 检查是否需要开启会话
     * @Author: lixin
     * @Date: 下午3:44 2017/12/22
     */
    public void checkAndStart(){
        CuratorFrameworkState state = handler.getState();
        if(CuratorFrameworkState.LATENT == state){
            start();
        }
    }

    /**
     * @Description: 开启会话
     * @Author: lixin
     * @Date: 下午12:59 2017/12/19
     */
    public void start(){
        logger.info("zookeeper client connecting...");
        this.handler.start();
    }

    /**
     * @Description: 关闭会话
     * @Author: lixin
     * @Date: 下午12:59 2017/12/19
     */
    public void close(){
        logger.info("zookeeper client close...");
        this.handler.close();
    }

    /**
     * @Description: 创建空节点
     * @Author: lixin
     * @Date: 下午3:25 2017/12/19
     */
    public boolean createEmptyNode(ZkNode zkNode) throws Exception {
        Checker.checkNode(zkNode);
        this.create(zkNode);
        return null != this.handler.checkExists().forPath(zkNode.getPath());
    }

    /**
     * @Description: 创建节点
     * @Author: lixin
     * @Date: 下午3:25 2017/12/19
     */
    public boolean createNodeData(ZkNode zkNode)throws Exception{
        Checker.checkNodeData(zkNode);
        this.create(zkNode);
        return null != this.handler.checkExists().forPath(zkNode.getPath());
    }

    /**
     * @Description: 创建临时空节点
     * @Author: lixin
     * @Date: 下午3:26 2017/12/19
     */
    public boolean createTempNode(ZkNode zkNode)throws Exception{
        Checker.checkNode(zkNode);
        zkNode.setNodeModel(CreateMode.EPHEMERAL);
        this.create(zkNode);
        return null != this.handler.checkExists().forPath(zkNode.getPath());
    }

    /**
     * @Description: 创建临时节点
     * @Author: lixin
     * @Date: 下午3:26 2017/12/19
     */
    public boolean createTempNodeData(ZkNode zkNode)throws Exception{
        Checker.checkNodeData(zkNode);
        zkNode.setNodeModel(CreateMode.EPHEMERAL);
        this.create(zkNode);
        return null != this.handler.checkExists().forPath(zkNode.getPath());
    }

    /**
     * @Description: 创建节点并设置节点data，所有节点均为持久节点
     * @Author: lixin
     * @Date: 下午3:01 2017/12/19
     */
    public void create(ZkNode zkNode) throws Exception {
        logger.info("zkNode create [{}]", zkNode);
        //设置listener
        if(zkNode.isChildListen()) ChildrenListener.listen(this.handler, zkNode.getPath(), zkNode.getChildrenChangedCallback());
        if(!exist(zkNode.getPath())){
            this.handler.create()
                    .creatingParentsIfNeeded()
                    .withMode(zkNode.getNodeModel())
                    .withACL(zkNode.getDigestAacl())
                    .forPath(zkNode.getPath(), zkNode.getDataByte());
        }

        if(zkNode.isNodeListen()) NodeListener.listen(this.handler, zkNode.getPath(), zkNode.getNodeChangedCallback());
    }

    /**
     * @Description: 获取节点数据
     * @Author: lixin
     * @Date: 下午3:42 2017/12/19
     */
    public String getData(String path) throws Exception {
        return new String(this.handler.getData().forPath(path));
    }

    /**
     * @Description: 获取节点数据
     * @Author: lixin
     * @Date: 下午3:42 2017/12/19
     */
    public String getData(String path,Stat stat)throws Exception{
        return new String(this.handler.getData().storingStatIn(stat).forPath(path));
    }

    /**
     * @Description: 设置节点数据
     * @Author: lixin
     * @Date: 下午3:45 2017/12/19
     */
    public int setData(String path,String data,List<ACL> acls)throws Exception{
        Stat stat = this.handler.setData().forPath(path,data.getBytes());
        if(null != acls){
            setAcl(path,acls);
        }
        return stat.getVersion();
    }

    /**
     * @Description: 设置节点数据
     * @Author: lixin
     * @Date: 下午3:45 2017/12/19
     */
    public int setData(String path,String data,Stat stat,List<ACL> acls)throws Exception{
        stat = this.handler.setData().withVersion(stat.getVersion()).forPath(path,data.getBytes());
        if(null != acls){
            setAcl(path,acls);
        }
        return stat.getVersion();
    }

    private Stat setAcl(String path,List<ACL> acls) throws Exception {
        return this.handler.setACL().withACL(acls).forPath(path);
    }

    /**
     * @Description: 获取子节点
     * @Author: lixin
     * @Date: 下午3:45 2017/12/21
     */
    public List<String> getChildren(String path) throws Exception {
        return this.handler.getChildren().forPath(path);
    }

    public boolean exist(String path) throws Exception {
        return this.handler.checkExists().forPath(path) != null;
    }

    public boolean nodeListen(final String path,final INodeChangedCallback callback){
        try {
            NodeListener.listen(this.handler,path,callback);
            return true;
        } catch (Exception e) {
            logger.error("node listen exception. [path:{}]",path,e);
            return false;
        }
    }

    public boolean childrenListen(final String path,final IChildrenChangedCallback callback){
        try {
            ChildrenListener.listen(this.handler,path,callback);
            return true;
        } catch (Exception e) {
            logger.error("children listen exception. [path:{}]",path,e);
            return false;
        }
    }
}
