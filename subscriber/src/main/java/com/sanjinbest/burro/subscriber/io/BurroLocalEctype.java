package com.sanjinbest.burro.subscriber.io;

import com.alibaba.fastjson.JSONObject;
import com.sanjinbest.burro.core.util.Checker;
import com.sanjinbest.burro.core.zookeeper.BurroDefs;
import com.sanjinbest.burro.core.zookeeper.DataNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: lixin
 * @Date: 2017/12/25 上午11:24
 */
public class BurroLocalEctype {
    Logger logger = LoggerFactory.getLogger(BurroLocalEctype.class);

    private String localPath;

    public BurroLocalEctype(String localPath) {
        this.localPath = localPath;
    }

    /**
     * @Description: 将配置文件写入本地
     * @Author: lixin
     * @Date: 下午2:37 2017/12/25
     */
    public boolean writeConfig(String app, String env, String prop, DataNode node){
        String dir = filePath(app,env);
        if(nonExistAndMkdirs(dir)){
            String configPath = dir + File.separator + prop + BurroDefs.File.SUFFIX;
            File configFile = new File(configPath);
            if(null != configFile && configFile.exists() && configFile.isFile()){
                configFile.delete();
            }

            FileWriter writer = null;
            try {
                writer = new FileWriter(configFile);
                writer.write(JSONObject.toJSONString(node));
                return true;
            } catch (IOException e) {
                logger.error("文件写入异常。file:{}",configFile,e);
                return false;
            }finally {
                if(null != writer){
                    try {
                        writer.close();
                    } catch (IOException e) {
                        logger.error("关闭文件写入流异常。file:{}",configPath,e);
                    }
                }
            }
        }else{
            logger.error("写入配置到本地失败。app:{},env:{},prop:{}",app,env,prop);
            return false;
        }
    }

    /**
     * @Description: 读取环境下所有配置
     * @Author: lixin
     * @Date: 下午8:01 2017/12/26
     */
    public List<DataNode> readAllConfig(String app,String env){
        String dirPath = filePath(app,env);
        File envDir = new File(dirPath);
        if(null == envDir || !envDir.exists() || !envDir.isDirectory()){
            logger.error("没有可读取的本地配置目录。app:{},env:{}",app,env);
            return null;
        }

        String[] files = envDir.list();
        if(null != files && files.length > 0){
            List<DataNode> nodes = new ArrayList<DataNode>();
            for(int i=0;i<files.length;i++){
                DataNode dataNode = readConfig(app, env, files[i]);
                if(null == dataNode)continue;
                nodes.add(dataNode);
            }
            return nodes;
        }
        return null;

    }

    /**
     * @Description: 读取本地配置
     * @Author: lixin
     * @Date: 下午4:01 2017/12/25
     */
    public DataNode readConfig(String app, String env, String prop){
        if(Checker.invalidString(prop) || prop.lastIndexOf(BurroDefs.File.SUFFIX) < 0)return null;
        File configFile = new File(filePath(app,env,prop));
        if(null == configFile || !configFile.exists() || !configFile.isFile()){
            logger.error("没有可读取的本地配置。app:{},env:{},prop:{}",app,env,prop);
            return null;
        }

        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            reader = new BufferedReader(new FileReader(configFile));
            while(Checker.validString(line = reader.readLine())){
                sb.append(line);
            }

            if(sb.length() > 0){
                return JSONObject.parseObject(sb.toString(),DataNode.class);
            }

        } catch (Exception e) {
            logger.error("文件读取异常。file:{}",configFile,e);
            return null;
        }finally {
            if(null != reader){
                try {
                    reader.close();
                } catch (IOException e) {
                    logger.error("关闭文件读取流异常。file:{}",filePath(app,env,prop),e);
                }
            }
        }

        return null;

    }

    private boolean nonExistAndMkdirs(String path){
        File dir = new File(path);
        if(!dir.exists()){
            return dir.mkdirs();
        }
        return true;
    }

    private String filePath(String ... strs){
        StringBuilder sb = new StringBuilder(localPath);
        for(String str : strs){
            if(str.indexOf(File.separator) < 0){
                sb.append(File.separator).append(str);
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        File file = new File("/tmp_soft/app/env");
        System.out.println(file.list()[0]);
        System.out.println(file.listFiles().length);
    }
}
