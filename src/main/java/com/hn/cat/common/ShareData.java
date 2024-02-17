package com.hn.cat.common;

import java.io.File;
import java.util.*;

public class ShareData {
    // 工具类实例对象
    private static volatile ShareData instance;
    // 老婆列表
    private LinkedHashMap<Long, Long> MarryMap;
    //订婚列表
    private List<Long> DingHunList;
    //离婚列表
    private List<Long> LiHunList;
    //小三列表
    private List<Long> XiaoSanList;
    //获取有权限的群
    private List<Integer> Groups;
    //配置文件
    private File ResolveConfigFile;
    public static ShareData getInstance(){
        if (instance == null){
            synchronized (ShareData.class){
                if (instance == null){
                    instance = new ShareData();
                    //初始化群老婆列表
                    instance.setMarryMap(new LinkedHashMap<>());
                    // 初始化订婚
                    instance.setDingHunList(new ArrayList<>());
                    // 初始化小三
                    instance.setXiaoSanList(new ArrayList<>());
                    // 初始化离婚
                    instance.setLiHunList(new ArrayList<>());
                }
            }
        }
        return instance;
    }

    public LinkedHashMap<Long, Long> getMarryMap() {
        return MarryMap;
    }

    public void setMarryMap(LinkedHashMap<Long, Long> marryMap) {
        MarryMap = marryMap;
    }

    public List<Long> getDingHunList() {
        return DingHunList;
    }

    public void setDingHunList(List<Long> dingHunList) {
        DingHunList = dingHunList;
    }

    public List<Long> getLiHunList() {
        return LiHunList;
    }

    public void setLiHunList(List<Long> liHunList) {
        LiHunList = liHunList;
    }

    public List<Long> getXiaoSanList() {
        return XiaoSanList;
    }

    public void setXiaoSanList(List<Long> xiaoSanList) {
        XiaoSanList = xiaoSanList;
    }


    public List<Integer> getGroups() {
        return Groups;
    }

    public void setGroups(List<Integer> groups) {
        Groups = groups;
    }


    public File getResolveConfigFile() {
        return ResolveConfigFile;
    }

    public void setResolveConfigFile(File resolveConfigFile) {
        ResolveConfigFile = resolveConfigFile;
    }


}

