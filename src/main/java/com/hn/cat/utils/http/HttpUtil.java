package com.hn.cat.utils.http;

import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.utils.ExternalResource;
import okhttp3.*;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {
    private static InputStream input;
    private static ExternalResource externalResource;
    static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().build();
    //获取用户QQ头像
    public static String getQQImage(Long qq, MessageEvent event) {
        try {
            URL url = new URL("http://q.qlogo.cn/g?b=qq&nk="+qq+"&s=640");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            input = con.getInputStream();
            externalResource = ExternalResource.create(input);
            Contact contact = event.getSubject();
            Image image = contact.uploadImage(externalResource);
            return image.getImageId();
        }catch(Exception e){
            String message = e.getMessage();
            return "图片获取失败"+message;
        }finally {
            try {
                input.close();
                externalResource.close();
            }catch (Exception e){
                System.out.println("资源关闭失败");
            }
        }
    }
}
