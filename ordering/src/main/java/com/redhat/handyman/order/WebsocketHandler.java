package com.redhat.handyman.order;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A handler that both receives events from Kafka and translate them to the subscribed
 * websocket sessions.
 * @author laurent
 */
@ServerEndpoint("/websocket")
@ApplicationScoped
public class WebsocketHandler {

   /** Get a JBoss logging logger. */
   private final Logger logger = Logger.getLogger(getClass());

   private Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());

   @OnOpen
   public void onOpen(Session session) {
      sessions.add(session);
      logger.info("WebSocket " + session.getId() + " connected");
   }

   @OnClose
   public void onClose(Session session) {
      sessions.remove(session);
      logger.info("WebSocket " + session.getId() + " disconnected");
   }

   @OnError
   public void onError(Session session, Throwable throwable) {
      sessions.remove(session);
      logger.info("WebSocket " + session.getId() + " disconnected");
   }

   @OnMessage
   public void onMessage(String message) {
      // Nothing to do here...
   }

   @Incoming("rendering-status")
   public void onRenderingStatus(String message) {
      logger.info("Receiving a rendering-status Kafka message: " + message);
      logger.infof("Propagating it to %d sessions", sessions.size());
      sessions.forEach(s -> {
         s.getAsyncRemote().sendObject(message, result ->  {
            if (result.getException() != null) {
               logger.error("Unable to send message",result.getException());
            }
         });
      });
   }

   @Incoming("rendering-results")
   public void onRenderingResponse(String message) {
      logger.info("Receiving a rendering-responses Kafka message: " + message);
      sessions.forEach(s -> {
         s.getAsyncRemote().sendObject(message, result ->  {
            if (result.getException() != null) {
               logger.error("Unable to send message",result.getException());
            }
         });
      });
   }
}
