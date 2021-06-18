package com.redhat.handyman.order;

import java.io.File;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

/**
 * JAX-RS resource for managing download of a project file from the S3 bucket.
 * @author laurent
 */
@Path("/download")
public class FileDownloadResource {

   /** Get a JBoss logging logger. */
   private final Logger logger = Logger.getLogger(getClass());

   @ConfigProperty(name = "blendfilefolder")
   String blendfilefolder;

   @GET
   @Path("{name}")
   @Consumes(MediaType.APPLICATION_OCTET_STREAM)
   public Response download(@PathParam("name") String name) {
      logger.infof("Starting download of '%s'", name);

      return Response.ok(new File(blendfilefolder + "/" + name))
            .header(HttpHeaders.CONTENT_DISPOSITION, name).build();
   }
}
