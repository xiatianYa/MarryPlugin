package com.hn.cat.utils.timing;

import com.hn.cat.common.ShareData;
import com.hn.cat.utils.file.FileUtil;

import java.io.File;
import java.util.*;

public class TimingUtil {
    //每天0点调用的定时任务
    public void startTimedTasks(int Hour,int Minute,int Second,File MapFile){
        ShareData instance = ShareData.getInstance();
        // 获取当前日期和时间
        Calendar calendar = Calendar.getInstance();
        // 设置定时任务的时间
        calendar.set(Calendar.HOUR_OF_DAY, Hour);
        calendar.set(Calendar.MINUTE, Minute);
        calendar.set(Calendar.SECOND, Second);
        calendar.set(Calendar.MILLISECOND, 0);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                //清空群老婆列表
                instance.setMarryMap(new LinkedHashMap<>());
                System.out.println("0点定时任务被调用了..");
            }
        };
        Timer timer=new Timer();
        //重复24小时
        timer.scheduleAtFixedRate(task,calendar.getTime(),24*60*60*1000);
    }
    //每五分钟调用的定时器任务
    public void startFiveMinutesTasks(File MapFile){
        ShareData instance = ShareData.getInstance();
        // 创建一个定时器
        Timer timer = new Timer();
        // 创建一个定时器任务
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                // 保存娶群友列表
                LinkedHashMap<Long, Long> marryMap = instance.getMarryMap();
                if (!marryMap.isEmpty()){
                    FileUtil.saveMap(marryMap,MapFile);
                }
                // 删除订婚
                instance.setDingHunList(new ArrayList<>());
                // 删除小三
                instance.setXiaoSanList(new ArrayList<>());
                // 删除离婚
                instance.setLiHunList(new ArrayList<>());
                System.out.println("180分钟定时任务被调用...");
            }
        };
        // 设置定时器任务的时间间隔3小时
        timer.scheduleAtFixedRate(task, 0, 180*60*1000);
    }
}

