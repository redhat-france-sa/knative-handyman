package com.redhat.handyman.order;

import io.smallrye.reactive.messaging.kafka.OutgoingKafkaRecordMetadata;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * JAX-RS resource for managing order options and submission.
 * @author laurent
 */
@Path("/order")
public class OrderResource {

   /** Get a JBoss logging logger. */
   private final Logger logger = Logger.getLogger(getClass());

   @Inject
   RenderingService renderingService;

   @Inject
   @Channel("rendering-requests")
   Emitter<RenderingRequest> renderingRequestPublisher;

   @POST
   @Path("options")
   public List<RenderingOption> getOptions(FileObject fileObject) {
      logger.infof("Getting rendering options for '%s'", fileObject.getKey());
      return renderingService.computeRenderingOptions(fileObject);
   }

   @POST
   @Path("rendering")
   public Response orderRendering(Order order) {
      logger.infof("Receiving an order '%s'", order);
      if (renderingService.isRenderingOptionValid(order.getFileObject(), order.getOption())) {
         logger.infof("Ordering a rendering for '%s'", order.getFileObject().getKey());

         // Initialize a response.
         String renderingId = UUID.randomUUID().toString();
         RenderingResponse response = new RenderingResponse();
         response.setCreatedOn(new Date());
         response.setResponseId(renderingId);
         response.setChosenOption(order.getOption());

         // Creating the number of requests corresponding to frame dividers.
         int z = 0;
         for (int x=0; x<order.getOption().getFrameDividers(); x++) {
            for (int y=0; y<order.getOption().getFrameDividers(); y++) {
               RenderingRequest request = new RenderingRequest();
               request.setRenderingId(renderingId);
               request.setObjectKey(order.getFileObject().getKey());
               request.setAreaX(x);
               request.setAreaY(y);
               request.setSamples(order.getOption().getSamples());
               request.setFrameDivider(order.getOption().getFrameDividers());
               request.setResolutionX(order.getOption().getResolutionX());
               request.setResolutionY(order.getOption().getResolutionY());
               // Publishing Rendering request on Kafka.
               OutgoingKafkaRecordMetadata metadata = OutgoingKafkaRecordMetadata.builder()
                     .withTopic("rendering-requests")
                     .withPartition(z++)
                     .build();
               renderingRequestPublisher.send(Message.of(request).addMetadata(metadata));
               //renderingRequestPublisher.send(request);
            }
         }
         return Response.ok(response).build();
      }
      return Response.serverError().build();
   }
}
