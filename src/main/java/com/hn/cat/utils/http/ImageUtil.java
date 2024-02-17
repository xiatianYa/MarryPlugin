package com.hn.cat.utils.http;

import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.utils.ExternalResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ImageUtil {
    public static String getImage(String path, MessageEvent event){
        File file=new File(path);
        ExternalResource externalResource = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            externalResource = ExternalResource.create(fileInputStream);
            Contact contact = event.getSubject();
            Image image = contact.uploadImage(externalResource);
            return image.getImageId();
        } catch (IOException e) {
            return null;
        }finally {
            if (externalResource != null) {
                try {
                    externalResource.close();
                } catch (IOException e) {
                    System.out.println("本地图片获取失败"+e.getMessage());
                }
            }
        }
    }
}
