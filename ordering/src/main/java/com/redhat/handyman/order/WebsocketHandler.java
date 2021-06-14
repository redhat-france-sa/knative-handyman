package com.redhat.handyman.order;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.jboss.logging.Logger;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@ServerEndpoint("/websocket")
@ApplicationScoped
public class WebsocketHandler {

   /** Get a JBoss logging logger. */
   private final Logger logger = Logger.getLogger(getClass());

   private Set<Session> sessions = Collections.synchronizedSet(new HashSet<Session>());

   @OnOpen
   public void onOpen(Session session) {
      sessions.add(session);
      logger.info(session.getId() + " connected");
   }

   @OnClose
   public void onClose(Session session) {
      sessions.remove(session);
      logger.info(session.getId() + " disconnected");
   }

   @OnError
   public void onError(Session session, Throwable throwable) {
      sessions.remove(session);
      logger.info(session.getId() + " disconnected");
   }

   @OnMessage
   public void onMessage(String message) {
      broadcast(message);
   }

   private void broadcast(String message) {
      sessions.forEach(s -> {
         s.getAsyncRemote().sendObject(message, result ->  {
            if (result.getException() != null) {
               logger.error("unable to send message",result.getException());
            }
         });
      });
   }
}
