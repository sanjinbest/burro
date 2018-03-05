package com.sanjinbest.burro.core.zookeeper;

import com.alibaba.fastjson.JSONObject;
import com.sanjinbest.burro.core.path.PathTools;
import com.sanjinbest.burro.core.util.Checker;

import java.io.Serializable;
import java.util.*;

/**
 * @Description: 节点，一般用于存储节点数据，如创建节点请使用{@link ZkNode}
 * @Author: lixin
 * @Date: 2017/12/20 下午3:12
 */
public class DataNode{

    private String path;

    private Map<String,String> KV;

    private String version;

    private Set<String> subscribers;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<String, String> getKV() {
        return KV;
    }

    public void setKV(Map<String, String> KV) {
        this.KV = KV;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Set<String> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(Set<String> subscribers) {
        this.subscribers = subscribers;
    }

    public void addSubscriber(String subscriber) {
        if(null == subscribers)subscribers = new HashSet<String>();
        this.subscribers.add(subscriber);
    }
    public void addKV(String key,String value){
        if(Checker.validString(key) && Checker.validString(value)){
            if(null == KV)KV = new HashMap<String, String>();
            KV.put(key,value);
        }
    }

    public String KV2String(){
        return JSONObject.toJSONString(KV);
    }

    public void string2KV(String str){
        if(Checker.validString(str)) {
            setKV((Map<String, String>) JSONObject.parse(str));
        }
    }

    public void string2Data(String str){
        if(Checker.validString(str)) {
            JSONObject json = JSONObject.parseObject(str);
            if(json.containsKey(PathTools.KV)){
                setKV(json.getObject(PathTools.KV,Map.class));
            }
            if(json.containsKey(PathTools.VERSION)){
                setVersion(json.getString(PathTools.VERSION));
            }
        }
    }

    public String Data2String(){
        JSONObject json = new JSONObject();
        json.put(PathTools.KV,KV);
        json.put(PathTools.VERSION,version);
        return json.toJSONString();
    }

    public String subscribers2String(){
        return JSONObject.toJSONString(subscribers);
    }

    public void string2Subscribers(String str){
        if(Checker.validString(str)){
            setSubscribers(JSONObject.parseObject(str,Set.class));
        }
    }

    public boolean valid(){
        return Checker.validString(this.path) && null != this.KV && this.KV.size() > 0 && Checker.validString(this.version);
    }

    @Override
    public String toString() {
        return "DataNode{" +
                "path='" + path + '\'' +
                ", KV=" + KV +
                ", version='" + version + '\'' +
                ", subscribers=" + subscribers +
                '}';
    }
}
