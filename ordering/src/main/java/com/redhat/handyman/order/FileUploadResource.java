package com.redhat.handyman.order;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * JAX-RS resource for managing upload of a project file on the S3 bucket.
 * @author laurent
 */
@Path("/upload")
public class FileUploadResource {

   /** Get a JBoss logging logger. */
   private final Logger logger = Logger.getLogger(getClass());

   @Inject
   S3Client s3Client;

   @ConfigProperty(name = "bucket.name")
   String bucketName;

   @POST
   @Path("{name}")
   @Consumes(MediaType.APPLICATION_OCTET_STREAM)
   public Response uploadFile(@PathParam("name") String name, @HeaderParam("content-length") long contentLength, InputStream stream) throws IOException {
      // Retrieve file and transfer to S3.
      logger.infof("Receiving upload of '%s'", name);

      // We do not need going through a temp file as we have content-length from header.
      //File uploadedFile = uploadToTemp(stream);
      //long contentLength = uploadedFile.length();

      logger.infof("Now transferring '%s' to our S3 bucket...", name);
      PutObjectRequest putRequest = buildPutRequest(name);
      PutObjectResponse putResponse = null;
      try {
         //putResponse = s3Client.putObject(putRequest, RequestBody.fromFile(uploadedFile));
         putResponse = s3Client.putObject(putRequest, RequestBody.fromInputStream(stream, contentLength));
      } catch (Exception e) {
         logger.error("Exception while putting object on S3 bucket", e);
         return Response.serverError().build();
      } finally {
         // Removing local temporary file.
         //uploadedFile.delete();
      }

      logger.infof("'%s' transferred on S3 bucket", name);
      FileObject fileObj = buildFileObject(putRequest, putResponse);
      fileObj.setSize(contentLength);

      return Response.accepted(fileObj).build();
   }


   /** Upload to temporary file before moving to S3.  */
   protected File uploadToTemp(InputStream data) {
      File tempPath;
      try {
         tempPath = File.createTempFile("handyman-order-upload", ".tmp");
         Files.copy(data, tempPath.toPath(), StandardCopyOption.REPLACE_EXISTING);
      } catch (Exception ex) {
         throw new RuntimeException(ex);
      }
      return tempPath;
   }

   /** Build an Amanzon S3> Put request. */
   protected PutObjectRequest buildPutRequest(String fileName) {
      return PutObjectRequest.builder()
            .bucket(bucketName)
            .key("uploaded/" + fileName + '-' + System.currentTimeMillis())
            .contentType("application/octet-stream")
            .build();
   }

   /** Build a FileObject from Amazon objects. */
   protected static FileObject buildFileObject(PutObjectRequest putRequest, PutObjectResponse putResponse) {
      FileObject file = new FileObject();
      file.setKey(putRequest.key());
      file.setETag(putResponse.eTag());
      return file;
   }
}
