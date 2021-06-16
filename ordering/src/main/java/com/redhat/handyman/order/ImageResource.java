package com.redhat.handyman.order;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.FileUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Path("/renders")
public class ImageResource {

    @ConfigProperty(name = "blenderqueue.render.files.local.folder") 
    public String renderCollectorfolder;

    @GET
    @Path("/tiles/{fname}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response image(@PathParam("fname") String fname ) throws IOException {
        FileInputStream fis =null;
        fis = new FileInputStream(new File(renderCollectorfolder+"/"+fname));
        return Response.ok(fis).build();
    }

    @POST
    @Path("/upload/{name}")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    public Response fileUpload(@PathParam("name") String name ,InputStream image) throws IOException {
        
        FileUtils.copyInputStreamToFile(image, new File(renderCollectorfolder+"/"+name));
        return Response.ok("ok").build();
    }
}
