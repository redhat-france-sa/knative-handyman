package com.redhat.handyman.order;

import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class RenderingService {

   /** Get a JBoss logging logger. */
   private final Logger logger = Logger.getLogger(getClass());

   /**
    *
    * @param fileObject
    * @return
    */
   public List<RenderingOption> computeRenderingOptions(FileObject fileObject) {
      List<RenderingOption> options = new ArrayList<>();

      logger.infof("Computing rendering options for '%s' of size %d", fileObject.getKey(), fileObject.getSize());
      if (fileObject.getSize() <= 100000) {
         options.add(new RenderingOption("S", 1, 4, 2, 640, 360));
         options.add(new RenderingOption("M", 2, 10, 3, 640, 360));
         options.add(new RenderingOption("L", 4, 100, 4, 640, 360));
      } else if (fileObject.getSize() > 100000 && fileObject.getSize() < 100000000) {
         options.add(new RenderingOption("S", 2, 4, 2, 1280, 720));
         options.add(new RenderingOption("M", 5, 10, 3, 1280, 720));
         options.add(new RenderingOption("L", 10, 100, 4, 1280, 720));
      } else {
         options.add(new RenderingOption("S", 4, 4, 2, 1280, 720));
         options.add(new RenderingOption("M", 10, 10, 3, 1280, 720));
         options.add(new RenderingOption("L", 12, 100, 4, 1280, 720));
      }
      return options;
   }

   /**
    *
    * @param fileObject
    * @param option
    * @return
    */
   public boolean isRenderingOptionValid(FileObject fileObject, RenderingOption option) {
      List<RenderingOption> candidates = computeRenderingOptions(fileObject);
      return candidates.contains(option);
   }
}
