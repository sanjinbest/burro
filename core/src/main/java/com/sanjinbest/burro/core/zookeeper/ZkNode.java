package com.sanjinbest.burro.core.zookeeper;

import com.sanjinbest.burro.core.listener.IChildrenChangedCallback;
import com.sanjinbest.burro.core.listener.INodeChangedCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 节点，一般用于创建时使用。如存储数据请使用{@link DataNode}
 * @Author: lixin
 * @Date: 2017/12/19 下午4:39
 */
public class ZkNode {

    private String path;

    private String data = "";

    private int version = -1;

    private boolean nodeListen = false;

    private boolean childListen = false;

    private INodeChangedCallback nodeChangedCallback;

    private IChildrenChangedCallback childrenChangedCallback;

    private CreateMode nodeModel = CreateMode.PERSISTENT;

    private Stat stat = new Stat();

    private List<ACL> digestAcl;

    public ZkNode(String path, String data, Stat stat, List<ACL> digestAcl) {
        this.path = path;
        this.data = data;
        this.stat = stat;
        this.digestAcl = digestAcl;
    }

    public ZkNode(String path, String data, List<ACL> digestAcl) {
        this.path = path;
        this.data = data;
        this.digestAcl = digestAcl;
    }

    public ZkNode(String path, List<ACL> digestAcl){
        this.path = path;
        this.digestAcl = digestAcl;
    }

    public String getPath() {
        return path;
    }

    protected void setPath(String path) {
        this.path = path;
    }

    public String getData() {
        return data;
    }

    protected byte[] getDataByte(){
        return null != data ? data.getBytes() : "".getBytes();
    }

    protected void setData(String data) {
        this.data = data;
    }

    protected int getVersion() {
        return version;
    }

    protected void setVersion(int version) {
        this.version = version;
    }

    protected boolean isNodeListen() {
        return nodeListen;
    }

    protected void openNodeListener() {
        this.nodeListen = true;
    }

    protected boolean isChildListen() {
        return childListen;
    }

    protected void openChildrenListener() {
        this.childListen = true;
    }

    protected INodeChangedCallback getNodeChangedCallback() {
        return nodeChangedCallback;
    }

    protected void setNodeChangedCallback(INodeChangedCallback nodeChangedCallback) {
        this.nodeChangedCallback = nodeChangedCallback;
    }

    protected IChildrenChangedCallback getChildrenChangedCallback() {
        return childrenChangedCallback;
    }

    protected void setChildrenChangedCallback(IChildrenChangedCallback childrenChangedCallback) {
        this.childrenChangedCallback = childrenChangedCallback;
    }

    protected CreateMode getNodeModel() {
        return nodeModel;
    }

    protected void setNodeModel(CreateMode nodeModel) {
        this.nodeModel = nodeModel;
    }


    public Stat getStat() {
        return stat;
    }

    public void setStat(Stat stat) {
        this.stat = stat;
    }

    public List<ACL> getDigestAacl() {
        return digestAcl;
    }

    public void setDigestAcl(List<ACL> digestAcl) {
        this.digestAcl = digestAcl;
    }

    @Override
    public String toString() {
        return "ZkNode{" +
                "path='" + path + '\'' +
                ", data='" + data + '\'' +
                ", version=" + version +
                ", nodeListen=" + nodeListen +
                ", childListen=" + childListen +
                ", nodeChangedCallback=" + nodeChangedCallback +
                ", childrenChangedCallback=" + childrenChangedCallback +
                ", nodeModel=" + nodeModel +
                '}';
    }
}
