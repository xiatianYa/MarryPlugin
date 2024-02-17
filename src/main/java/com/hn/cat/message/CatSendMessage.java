package com.hn.cat.message;

import com.hn.cat.Entry.MarryEntry;
import com.hn.cat.common.ShareData;
import com.hn.cat.utils.http.HttpUtil;
import com.hn.cat.utils.random.RandomUtil;
import net.mamoe.mirai.contact.ContactList;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.*;

import java.time.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.jetbrains.annotations.NotNull;

public class CatSendMessage {
    //处理初始数据
    public static Map<String,String> getMatcher(@NotNull GroupMessageEvent event){
        Map<String, String> map=new HashMap<>();
        String text = event.getMessage().get(0).toString();
        String regex = "mirai:source:ids=\\[(.*?)\\], internalIds=\\[(.*?)\\], from group (.*?) to (.*?) at (.*?)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            map.put("源ID",matcher.group(1));
            map.put("内部ID",matcher.group(2));
            map.put("来源QQ号",matcher.group(3));
            map.put("目标组ID",matcher.group(4));
            map.put("时间戳",matcher.group(5));
        }
        return map;
    }
    //判断当前QQ号是不是在群老婆列表中
    public static MarryEntry IfWife(Long qq){
        ShareData instance = ShareData.getInstance();
        //获取群老婆列表
        LinkedHashMap<Long, Long> map = instance.getMarryMap();
        for (Long key : map.keySet()) {
            Long value = map.get(key);
            if (key.equals(qq) || value.equals(qq)){
                return MarryEntry.builder()
                        .HusbandId(key)
                        .WifeId(value)
                        .build();
            }
        }
        //此人没有老婆 或 老公
        return null;
    }
    //发送自己当老婆的信息
    public static void sentMarryMessageBySelf(@NotNull GroupMessageEvent event, Long husbandId, Long wifeId){
        //获取老公照片
        String qqImage = HttpUtil.getQQImage(husbandId, event);
        Group group = event.getGroup();
        //获取老公
        NormalMember husBand = group.get(husbandId);
        if (husBand==null){
            event.getSender().sendMessage("Id获取失败!!!");
            return;
        }
        String husBandName = husBand.getNick();
        //构建消息
        MessageChain singleMessages = new MessageChainBuilder()
                .append(new At(wifeId))
                .append(new PlainText(" 你被娶了,你的群老公是: "))
                .append(new PlainText(husBandName))
                .append(Image.fromId(qqImage))
                .build();
        event.getSubject().sendMessage(singleMessages);
    }
    //发送自己当老公信息
    public static void sentHusbandMessageBySelf(@NotNull GroupMessageEvent event, Long husbandId, Long wifeId){
        //获取老婆照片
        String qqImage = HttpUtil.getQQImage(wifeId, event);
        Group group = event.getGroup();
        //获取妻子
        NormalMember Wife = group.get(wifeId);
        if (Wife==null){
            event.getSender().sendMessage("Id获取失败!!!");
            return;
        }
        String wifeName = Wife.getNick();
        //构建消息
        MessageChain singleMessages = new MessageChainBuilder()
                .append(new At(husbandId))
                .append(new PlainText(" 你的群老婆是: "))
                .append(new PlainText(wifeName))
                .append(Image.fromId(qqImage))
                .build();
        event.getSubject().sendMessage(singleMessages);
    }
    //获取所有群友信息列表
    public static List<Long> getContactList(@NotNull GroupMessageEvent event){
        //群友信息
        ContactList<NormalMember> members = event.getGroup().getMembers();
        //群友QQ号列表
        List<Long> memberList=new ArrayList<>();
        for (NormalMember member:members){
            //最后发言的时间单位为秒
            int lastSpeakTimestamp = member.getLastSpeakTimestamp();
            OffsetDateTime offsetDateTime  = OffsetDateTime.ofInstant(Instant.ofEpochSecond(lastSpeakTimestamp), ZoneId.systemDefault());
            ZonedDateTime zonedDateTime = offsetDateTime.atZoneSameInstant(ZoneId.systemDefault());
            //最后一次发言必须在一天以内 不在就排除掉
            if (!zonedDateTime.toLocalDate().isEqual(LocalDate.now())){
                continue;
            }else {
                //获取群友QQ号
                long id = member.getId();
                //添加进集合
                memberList.add(id);
            }
        }
        return memberList;
    }
    //发送当小三成功的信息
    public static void sentMistressMessage(@NotNull GroupMessageEvent event, Long husbandId, Long wifeId,Long fromQQ){
        Group group = event.getGroup();
        //变成了别人老公
        if (fromQQ.equals(husbandId)){
            //获取妻子
            NormalMember Wife = group.get(wifeId);
            if (Wife==null){
                event.getSender().sendMessage("QQ信息获取失败!!!");
                return;
            }
            //获取老婆名称
            String wifeName = Wife.getNick();
            //获取老婆照片
            String qqImage = HttpUtil.getQQImage(wifeId, event);
            //构建消息
            MessageChain singleMessages = new MessageChainBuilder()
                    .append(new At(husbandId))
                    .append(new PlainText(" 当小三成功,你的群老婆是: "))
                    .append(new PlainText(wifeName))
                    .append(Image.fromId(qqImage))
                    .build();
            event.getSubject().sendMessage(singleMessages);
        }else {
            //变成别人老婆
            NormalMember husBand = group.get(wifeId);
            if (husBand==null){
                event.getSender().sendMessage("Id获取失败!!!");
                return;
            }
            //获取老公名称
            String husBandName = husBand.getNick();
            //变成了别人老婆
            //获取老公照片
            String qqImage = HttpUtil.getQQImage(fromQQ, event);
            //构建消息
            MessageChain singleMessages = new MessageChainBuilder()
                    .append(new At(wifeId))
                    .append(new PlainText(" 当小三成功,你的群老公是: "))
                    .append(new PlainText(husBandName))
                    .append(Image.fromId(qqImage))
                    .build();
            event.getSubject().sendMessage(singleMessages);
        }
    }

    /**
     * 获取订婚需要的数据
     */
    public static Map<String,String> getEngage(@NotNull GroupMessageEvent event) {
        Map<String,String> map=new HashMap<>();
        if (event.getMessage().size()<2){
            return null;
        }
        String content = event.getMessage().get(2).toString();
        content = content.substring(1, content.length() - 1);  // 去除方括号
        String[] parts = content.split(":");  // 使用冒号作为分隔符
        if (parts.length>=2){
            map.put("type",parts[1]);
            map.put("qq",parts[2]);
            return map;
        }else {
            return null;
        }
    }
    //处理是否有老婆 是老公 是老婆
    public static void handleMarry(GroupMessageEvent event,MarryEntry marryEntry,List<Long> contactList,Long fromQQ){
        if (marryEntry==null){
            //没有老婆 或 老公 开始选老婆
            //统计数 每次为符合就+1 到达5次后 返回未娶到
            int count=0;
            while (true){
                if (count>=5){
                    event.getGroup().sendMessage("暂时没有合适对象,请稍后再试....");
                    break;
                }
                //获取随机数
                int randomNum = RandomUtil.RandomNumberByNext(contactList.size());
                //获得老婆QQ号
                Long wifeId = contactList.get(randomNum);
                //别人有老婆 或 老公 重新选
                if (IfWife(wifeId)==null){
                    //不能娶自己
                    if (!fromQQ.equals(wifeId)){
                        //添加到婚姻登记处
                        ShareData.getInstance().getMarryMap().put(fromQQ,wifeId);
                        sentHusbandMessageBySelf(event,fromQQ,wifeId);
                        return;
                    }
                }
                count++;
            }
        } else if (marryEntry.getHusbandId().equals(fromQQ)){
            //自己是老公
            sentHusbandMessageBySelf(event, marryEntry.getHusbandId(), marryEntry.getWifeId());
        } else if (marryEntry.getWifeId().equals(fromQQ)) {
            //自己是老婆
            sentMarryMessageBySelf(event,marryEntry.getHusbandId(), marryEntry.getWifeId());
        }
    }
    //娶群友
    public static void Marry(@NotNull GroupMessageEvent event){
        //起始信息
        Map<String, String> matcher = getMatcher(event);
        //群友QQ列表
        List<Long> contactList = getContactList(event);
        //发起者QQ号
        Long fromQQ= Long.valueOf(matcher.get("来源QQ号"));
        MarryEntry marryEntry = IfWife(fromQQ);
        handleMarry(event,marryEntry,contactList,fromQQ);
    }

    /**
     * 订婚
     */
    public static void Engage(GroupMessageEvent event) {
        ShareData instance = ShareData.getInstance();
        Map<String, String> engage = getEngage(event);
        //参数不符
        if (engage==null){
            return;
        }
        //起始信息
        Map<String, String> matcher = getMatcher(event);
        //群友QQ列表
        List<Long> contactList = getContactList(event);
        //发起者QQ号
        Long fromQQ= Long.valueOf(matcher.get("来源QQ号"));
        //被求婚者QQ
        Long qq = Long.valueOf(engage.get("qq"));
        //看看发起者有没有老婆 或 老婆
        MarryEntry marryEntry = IfWife(fromQQ);
        //看看被请求者有没有老公 或 老婆
        MarryEntry BymarryEntry = IfWife(qq);
        //如果进入 则两方没有老婆
        if (marryEntry==null && BymarryEntry==null){
            //判断他今天有没有求婚
            List<Long> dingHunList = instance.getDingHunList();
            for (Long item : dingHunList) {
                if (item.equals(fromQQ)){
                    event.getGroup().sendMessage("最近订过婚过哦,不要太花心哦...");
                    return;
                }
            }
            //判断类型是不是@ 不是则不处理
            if (engage.get("type").equals("at")){
                Random rand = new Random();
                int result = rand.nextInt(100);  // 生成0-99的随机数
                if (result < 60) {
                    //添加到老婆列表中
                    instance.getMarryMap().put(fromQQ,qq);
                    //发送娶老婆信息
                    sentHusbandMessageBySelf(event,fromQQ,qq);
                } else {
                    event.getGroup().sendMessage("求婚失败了...");
                }
            }
            //添加到求婚列表
            instance.getDingHunList().add(fromQQ);
        }else {
            //其中一人有老婆老公
            handleMarry(event,marryEntry,contactList,fromQQ);
        }
    }

    /**
     * 离婚
     */
    public static void Divorce(GroupMessageEvent event) {
        ShareData instance = ShareData.getInstance();
        //起始信息
        Map<String, String> matcher = getMatcher(event);
        //发起者QQ号
        Long fromQQ= Long.valueOf(matcher.get("来源QQ号"));
        //看看发起者有没有老婆 或 老婆
        MarryEntry marryEntry = IfWife(fromQQ);
        //查看是否今天离过婚
        List<Long> liHunList = instance.getLiHunList();
        for (Long id:liHunList){
            if (id.equals(fromQQ)){
                event.getGroup().sendMessage("你最近离过婚哦,不可以再离婚了...");
                return;
            }
        }
        if (marryEntry!=null){
            //随机函数
            Random rand = new Random();
            int result = rand.nextInt(100);  // 生成0-99的随机数
            if (result < 60) {
                //删除本地存储的结婚
                LinkedHashMap<Long, Long> marryMap = instance.getMarryMap();
                //丈夫ID
                Long husbandId = marryEntry.getHusbandId();
                marryMap.remove(husbandId);
                event.getGroup().sendMessage("离婚成功...");
            } else {
                //是老公
                if (marryEntry.getHusbandId().equals(fromQQ)){
                    event.getGroup().sendMessage("渣男,你老婆这么好,你竟然要离婚...");
                }else{
                    event.getGroup().sendMessage("渣女,你老公这么棒,你竟然要离婚...");
                }
            }
            //添加到离婚列表
            instance.getLiHunList().add(fromQQ);
        }else {
            event.getGroup().sendMessage("你还没有对象,快去娶一个吧...");
        }

    }

    /**
     * 当小三
     */
    public synchronized static void Mistress(GroupMessageEvent event) {
        ShareData instance = ShareData.getInstance();
        Map<String, String> engage = getEngage(event);
        //参数不符
        if (engage==null){
            return;
        }
        //起始信息
        Map<String, String> matcher = getMatcher(event);
        //发起者QQ号
        Long fromQQ= Long.valueOf(matcher.get("来源QQ号"));
        //被求婚者QQ
        Long qq = Long.valueOf(engage.get("qq"));
        //看看发起者有没有老婆 或 老婆
        MarryEntry marryEntry = IfWife(fromQQ);
        //看看被发起者有没有老婆 或 老公
        MarryEntry marry = IfWife(qq);
        //查看是否今天当过小三
        List<Long> List = instance.getXiaoSanList();
        if (List.contains(fromQQ)){
            event.getGroup().sendMessage("你最近当过小三了,不可以再当了...");
            return;
        }
        //当被发起者没有老婆或老公
        if (marry==null){
            event.getGroup().sendMessage("他还没有对象,快去追求吧...");
            return;
        }
        if (marryEntry!=null){
            if (marryEntry.getHusbandId().equals(fromQQ)){
                event.getGroup().sendMessage("你已经有老婆了,不能去当牛头人了...");
            }else {
                event.getGroup().sendMessage("你已经有老公了,不能去当牛头人了...");
            }
            return;
        }
        //随机函数
        Random rand = new Random();
        int result = rand.nextInt(100);  // 生成0-99的随机数
        if (result < 35) {
            //At是别人老公 则抢走他老婆
            if (marry.getHusbandId().equals(qq)){
                //移除民政局信息
                LinkedHashMap<Long, Long> marryMap = instance.getMarryMap();
                marryMap.remove(marry.getHusbandId());
                instance.getMarryMap().put(fromQQ,marry.getWifeId());
                sentMistressMessage(event,fromQQ,marry.getWifeId(),fromQQ);
            }else{
                //At是老婆 则抢走他老公
                //先删除他老公信息
                LinkedHashMap<Long, Long> marryMap = instance.getMarryMap();
                marryMap.remove(marry.getHusbandId());
                //添加发起者到老婆列表 结婚
                marryMap.put(marry.getHusbandId(),fromQQ);
                //发送结婚信息
                sentMistressMessage(event,marry.getHusbandId(),fromQQ,fromQQ);
            }
        } else {
            event.getGroup().sendMessage("当小三失败...");
        }
        //添加小三列表
        instance.getXiaoSanList().add(fromQQ);
    }

    /**
     * 群老婆列表
     */
    public static void getWifeList(GroupMessageEvent event) {
        ShareData instance = ShareData.getInstance();
        Group group = event.getGroup();
        LinkedHashMap<Long, Long> map = instance.getMarryMap();
        if (map.isEmpty()){
            event.getGroup().sendMessage("今天还没有人娶老婆呢...");
            return;
        }
        ForwardMessageBuilder MessageBuilder = new ForwardMessageBuilder(event.getGroup());
        map.forEach((husBand,wife)->{
            NormalMember husBandMember = group.get(husBand);
            NormalMember wifeMember = group.get(wife);
            MessageBuilder.add(event.getBot(),(chain)->{
                chain.add(new PlainText(husBandMember!=null ? husBandMember.getNick() : String.valueOf(husBand)));
                chain.add(new PlainText(" 你的群老婆是-> "));
                chain.add(new PlainText(wifeMember!=null ? wifeMember.getNick() : String.valueOf(wife)));
                return null;
            });
        });
        ForwardMessage build = MessageBuilder.build();
        event.getGroup().sendMessage(build);
    }
}
