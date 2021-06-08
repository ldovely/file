package com.example.boot.config;

import lombok.extern.slf4j.Slf4j;

import javax.websocket.*;

@ClientEndpoint()
@Slf4j
public class WebsocketClient {

    @OnOpen
    public void onOpen(Session session) {}

    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("客户端 {} 接收到消息:{}", session.getId(), message);
    }

    @OnClose
    public void onClose(Session session) {}
}
