package com.example.boot.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@ServerEndpoint("/test/test/{group}")
@Component
@Slf4j
public class CustomerWebsocketServer {

    /**
     * 存放所有在线的客户端
     */
    private static Map<String, Session> clients = new ConcurrentHashMap<>();

    /**
     * 客户端分组
     */
    private static Map<String, List<String>> groupMap = new ConcurrentHashMap<>();

    /**
     * 链接分组
     * @param group   组名
     * @param session session
     */
    @OnOpen
    public void onOpen(@PathParam("group") String group, Session session) {
        String sessionId = session.getId();
        //将新用户存入在线的组
        List<String> sessions = groupMap.get(group);
        if (CollectionUtils.isEmpty(sessions)) {
            sessions = new CopyOnWriteArrayList<>();
            sessions.add(sessionId);
        } else if (!sessions.contains(sessionId)) {
            sessions.add(sessionId);
        }

        groupMap.put(group, sessions);
        clients.put(sessionId, session);
    }

    /**
     * 客户端关闭
     *
     * @param session session
     */
    @OnClose
    public void onClose(Session session) {
        log.info("有用户断开了, id为:{}", session.getId());
        //将掉线的用户移除在线的组里
        clients.remove(session.getId());
        //todo 移除组别里的对应session
    }

    /**
     * 发生错误
     *
     * @param throwable e
     */
    @OnError
    public void onError(Throwable throwable) {
        //todo 异常处理
        throwable.printStackTrace();
    }

    /**
     * 收到客户端发来消息
     *
     * @param message 消息对象
     * @param session 当前session
     */
    @OnMessage
    public void onMessage(@PathParam("group") String group,String message, Session session) {
        log.info("group:{}",group);
        log.info("服务端收到客户端发来的消息: {}", message);
        //this.sendNoSelfAll(message + "sendNoSelfAll", session);
        //this.sendAll(message + "sendAll");
        //this.sendTo(message + "sendTo", session.getId());
        //this.sendToGroup(group,message);
        this.sendToGroupNoSelf(group,message,session);
    }

    /**
     * 按组发送
     *
     * @param group   组别
     * @param message 消息
     */
    private void sendToGroup(String group,String message){
        List<String> strings = groupMap.get(group);
        if (!CollectionUtils.isEmpty(strings)){
            strings.forEach(o->{
                clients.get(o).getAsyncRemote().sendText(message);
            });
        }
    }

    /**
     * 按组发送不包含自己
     *
     * @param group   组别
     * @param message 消息
     * @param session session
     */
    private void sendToGroupNoSelf(String group,String message,Session session){
        List<String> strings = groupMap.get(group);
        if (!CollectionUtils.isEmpty(strings)){
            strings.stream().filter(o->!Objects.equals(o, session.getId())).forEach(o->{
                //todo 提前清空session异常处理
                clients.get(o).getAsyncRemote().sendText(message);
            });
        }
    }

    /**
     * 发送消息给指定的人
     *
     * @param message   消息
     * @param sessionId sessionId
     */
    private void sendTo(String message, String sessionId) {
        clients.get(sessionId).getAsyncRemote().sendText(message);
    }

    /**
     * 群发消息
     *
     * @param message 消息内容
     */
    private void sendAll(String message) {
        clients.forEach((key, value) -> value.getAsyncRemote().sendText(message));
    }

    /**
     * 发送全部
     *
     * @param message 消息
     * @param session session
     */
    private void sendNoSelfAll(String message, Session session) {
        clients.entrySet().stream().filter(o -> !Objects.equals(o.getKey(), session.getId())).forEach(o -> {
            o.getValue().getAsyncRemote().sendText(message);
        });
    }
}
