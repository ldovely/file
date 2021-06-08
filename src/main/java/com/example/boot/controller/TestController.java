package com.example.boot.controller;

import com.example.boot.config.WebsocketClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;

@Controller
@Slf4j
public class TestController {


    @RequestMapping("test")
    @ResponseBody
    public String test(String text) throws Exception {
        Session session = null;
        Session session2 = null;
        Session session3 = null;
        try {
            session = getSession("group1");
            session2 = getSession("group2");
            session3 = getSession("group1");

            session.getBasicRemote().sendText(text);
        } catch (Exception e) {
            log.error("sendText error", e);
        }finally {
            if (null!=session){
                session.close();
            }
            if (null!=session2){
                session2.close();
            }
            if (null!=session3){
                session3.close();
            }
        }
        return text;
    }

    /**
     * 获取websocket client session
     *
     * @return
     */
    private Session getSession(String group) {
        String uri = "ws://localhost:8080/test/test/" + group;
        WebSocketContainer container;
        Session session = null;
        try {
            container = ContainerProvider.getWebSocketContainer();
            URI r = URI.create(uri);
            session = container.connectToServer(WebsocketClient.class, r);
        } catch (Exception e) {
            System.out.println("getSession error" + e);
        }
        return session;
    }
}
