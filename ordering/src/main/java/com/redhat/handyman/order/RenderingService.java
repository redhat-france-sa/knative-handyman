package com.redhat.handyman.order;

import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;

/**
 * RenderingService aggregated business services related to rendering options
 * proposition and validation.
 * @author laurent
 */
@ApplicationScoped
public class RenderingService {

   /** Get a JBoss logging logger. */
   private final Logger logger = Logger.getLogger(getClass());

   /**
    * Compute and propose rendering options depending on the file object.
    * @param fileObject The file to compute options from
    * @return A list of different options.
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
         options.add(new RenderingOption("S", 4, 10, 2, 1280, 720));
         options.add(new RenderingOption("M", 10, 100, 3, 1280, 720));
         options.add(new RenderingOption("L", 14, 512, 4, 1280, 720));
         options.add(new RenderingOption("XL", 20, 1024, 5, 1280, 720));
      }
      return options;
   }

   /**
    * Check is a rendering options is still valie.
    * @param fileObject The file the option is related to
    * @param option The chosen option from customer
    * @return True if chosen option is valid, false otherwise.
    */
   public boolean isRenderingOptionValid(FileObject fileObject, RenderingOption option) {
      logger.infof("Comparing option %s", option);
      List<RenderingOption> candidates = computeRenderingOptions(fileObject);
      candidates.forEach(o -> logger.infof("with %s", o.toString()));
      return candidates.contains(option);
   }
}
