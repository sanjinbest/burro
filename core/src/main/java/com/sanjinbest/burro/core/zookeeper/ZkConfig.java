package com.sanjinbest.burro.core.zookeeper;

import com.sanjinbest.burro.core.util.Checker;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.AuthInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: zookeeper 会话配置
 * @Author: lixin
 * @Date: 2017/12/20 下午2:06
 */
public class ZkConfig {

    private String host;
    private int sessionTimeout = BurroDefs.Session.SESSIONTIMEOUT;
    private int connectionTimeout = BurroDefs.Session.CONNECTIONTIMEOUT;
    private String nameSpace;
    private RetryPolicy retryPolicy = BurroDefs.Session.RETRY_POLICY;
    private List<AuthInfo> authInfos;

    public ZkConfig() {
    }

    protected void valid(){
        if(Checker.invalidString(host) || Checker.invalidString(nameSpace))
            throw new IllegalArgumentException("host or nameSpace is invalid param.");
    }

    protected String getHost() {
        return host;
    }

    protected int getSessionTimeout() {
        return sessionTimeout;
    }

    protected int getConnectionTimeout() {
        return connectionTimeout;
    }

    protected String getNameSpace() {
        return nameSpace;
    }

    protected RetryPolicy getRetryPolicy() {
        return retryPolicy;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setSessionTimeout(int sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public void setRetryPolicy(RetryPolicy retryPolicy) {
        this.retryPolicy = retryPolicy;
    }

    public void setAuthInfos(List<AuthInfo> authInfos) {
        this.authInfos = authInfos;
    }

    public void addDigestAuth(String username, String password){
        if(Checker.invalidString(username) || Checker.invalidString(password))throw new IllegalArgumentException("username or password param is null");
        if(null == authInfos)authInfos = new ArrayList<AuthInfo>();
        authInfos.add(new AuthInfo(BurroDefs.Auth.DIGEST,new String(username + ":" + password).getBytes()));
    }

    protected List<AuthInfo> getAuthInfos() {
        return authInfos;
    }
}
