package com.hn.cat.listener;

import com.hn.cat.message.CatSendMessage;
import com.hn.cat.common.ShareData;
import kotlin.coroutines.CoroutineContext;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CatEventHandler extends SimpleListenerHost {
    public CatEventHandler() {

    }

    @Override
    public void handleException(@NotNull CoroutineContext context, @NotNull Throwable exception) {
        super.handleException(context, exception);
    }

    @EventHandler
    public void onMessage(@NotNull GroupMessageEvent event) {
        //获取群消息
        MessageChain message = event.getMessage();
        //获取有权限的群列表
        List<Integer> groups = ShareData.getInstance().getGroups();
        //获取当前群号
        Integer group = Math.toIntExact(event.getGroup().getId());
        //判断群是否有权限
        if (groups.contains(group)) {
            if (message.get(1) instanceof PlainText) {
                String Type = ((PlainText) message.get(1)).getContent();
                send(event, Type);
            }
        }
    }
    //匹配发送信息
    public void send(GroupMessageEvent event, String Type) {
        switch (Type) {
            case "娶群友":
                CatSendMessage.Marry(event);
                break;
            case "订婚":
                CatSendMessage.Engage(event);
                break;
            case "离婚":
                CatSendMessage.Divorce(event);
                break;
            case "当小三":
                CatSendMessage.Mistress(event);
                break;
            case "群老婆列表":
                CatSendMessage.getWifeList(event);
                break;
            default:
                //未知信息
                break;
        }
    }
}
