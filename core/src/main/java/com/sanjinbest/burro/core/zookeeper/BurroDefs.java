package com.sanjinbest.burro.core.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * @Description:
 * @Author: lixin
 * @Date: 2017/12/20 下午2:34
 */
public class BurroDefs {

    public static class Path{
        public static final String BASE_NAMESPACE = "config-burro6";
    }

    public static class Session{
        public static final int SESSIONTIMEOUT = 60000;
        public static final int CONNECTIONTIMEOUT = 15000;
        public static final RetryPolicy RETRY_POLICY = new ExponentialBackoffRetry(1000,3);
    }

    /**
     * 超级管理员,在创建时会自动增加该用户
     * 这只是通过digest方式模拟了一个超级用户，如果该用户后续不能解决具体问题，可以通过zookeeper的超级用户作特殊操作
     * 开户超级管理员：zookeeper启动时增加：-Dzookeeper.DigestAuthenticationProvider.superDigest=username:password
     */
    public static class Auth{
        public static final String DIGEST = "digest";
        public static final String USERNAME = "root";
        public static final String PASSWORD = "root";
    }

    public static class File{
        public static final String SUFFIX=".burro";
    }
}
