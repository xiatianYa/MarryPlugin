package com.hn.cat.utils.file;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public class FileUtil {
    //保存一个Map到文件中保存
    public static void saveMap(LinkedHashMap<Long,Long> map,File file){
        if (!file.exists()){
            try {
                if(!file.createNewFile()){
                    System.out.println("文件创建失败!!!");
                }
            } catch (IOException e) {
                System.out.println("文件创建失败!!!");
            }
        }else {
            if(!file.delete()){
                System.out.println("文件删除失败!!!");
            }
        }
        FileOutputStream fileOutputStream=null;
        try {
            Properties properties=new Properties();
            for (Map.Entry<Long,Long> entry:map.entrySet()){
                properties.setProperty(entry.getKey().toString(),entry.getValue().toString());
            }
            fileOutputStream=new FileOutputStream(file);
            properties.store(fileOutputStream,"Map Data");
            System.out.println("文件保存成功!!!");
        }catch (IOException e){
            System.out.println("文件保存失败!!!");
        }finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                System.out.println("文件流关闭失败");
            }
        }
    }
    //读取一个文件到Map中
    public static LinkedHashMap<Long, Long> readMap(File mapPath){
        FileInputStream fileInputStream=null;
        try {
            //获取文件流
            fileInputStream=new FileInputStream(mapPath);
            //返回的Map集合
            LinkedHashMap<Long,Long> map=new LinkedHashMap<>();
            Properties loadedProperties=new Properties();
            loadedProperties.load(fileInputStream);
            for (String key: loadedProperties.stringPropertyNames()){
                Long husbandId= Long.valueOf(key);
                Long wifeId= Long.valueOf(loadedProperties.getProperty(key));
                map.put(husbandId,wifeId);
            }
            return map;
        }catch (IOException e){
            System.out.println("文件保存失败!!!");
            return null;
        }finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (IOException e) {
                System.out.println("文件流关闭失败");
            }
        }
    }
    /**
     * 获取文件base64编码
     *
     * @param imageUrl      文件路径
     * @param urlEncode 如果Content-Type是application/x-www-form-urlencoded时,传true
     * @return base64编码信息，不带文件头
     * @throws IOException IO异常
     */
    public static String getFileContentAsBase64(String imageUrl, boolean urlEncode) throws IOException {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.connect();
            InputStream is = connection.getInputStream();
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            byte[] imageBytes = baos.toByteArray();
            baos.close();
            is.close();
            String base64 = Base64.getEncoder().encodeToString(imageBytes);
            if (urlEncode) {
                base64 = URLEncoder.encode(base64, "utf-8");
            }
            return base64;
        } catch (IOException e) {
            System.out.println("网络图片获取失败:"+e.getMessage());
            return null;
        }
    }
}
