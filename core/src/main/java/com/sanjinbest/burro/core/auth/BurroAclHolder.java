package com.sanjinbest.burro.core.auth;

import com.sanjinbest.burro.core.zookeeper.BurroDefs;
import org.apache.curator.framework.api.ACLProvider;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: lixin
 * @Date: 2017/12/22 下午7:15
 */
public class BurroAclHolder implements ACLProvider{
    private List<ACL> acls;
    @Override
    public List<ACL> getDefaultAcl() {
        init();
        return acls;
    }

    @Override
    public List<ACL> getAclForPath(String path) {
        return acls;
    }

    public void addAcl(String user, String pwd) throws NoSuchAlgorithmException {
        init();
        acls.add(new ACL(ZooDefs.Perms.ALL,new Id(BurroDefs.Auth.DIGEST, DigestAuthenticationProvider.generateDigest(user+":"+pwd))));
    }

    public void clearAcl(){
        if(null != acls)acls.clear();
    }

    private void init(){
        if(null == acls){
            acls = new ArrayList<ACL>();
            acls.add(SUPER_MAN);
        }
    }

    private static ACL SUPER_MAN;

    static {
        try {
            SUPER_MAN = new ACL(ZooDefs.Perms.ALL,new Id(BurroDefs.Auth.DIGEST, DigestAuthenticationProvider.generateDigest(BurroDefs.Auth.USERNAME+":"+BurroDefs.Auth.PASSWORD)));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
