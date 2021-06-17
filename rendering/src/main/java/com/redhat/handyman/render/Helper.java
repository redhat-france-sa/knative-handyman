package com.redhat.handyman.render;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import org.apache.camel.ExchangeProperty;
import org.apache.camel.Header;
import org.apache.camel.language.simple.Simple;
import org.apache.commons.io.FileUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
@Named("helper")
public class Helper {
    

    @ConfigProperty(name = "blenderqueue.render.files.local.folder") 
    public String renderfolder;

    InputStream getLocalPictureAsStream(@Simple(value = "${body[filePath]}") String filpath) throws IOException{
       return new FileInputStream(new File(renderfolder+"/"+filpath));
    }


    Map<String,Object> imageDataFromPng(@Simple("${exchange.properties[origBody]['areaX']}") Integer x,@Simple("${exchange.properties[origBody]['areaY']}") Integer y,@Simple("${exchange.properties[origBody]['frameDivider']}") Integer frameDivider,@Simple("${exchange.properties[origBody]['resolutionX']}") Integer resolutionX,@Simple("${exchange.properties[origBody]['resolutionY']}") Integer resolutionY) throws IOException{
        // String imageAsBase64 = encodeFileToBase64(new File(renderCollectorfolder+"/"+filename));
        Map<String,Object> imageData = new LinkedHashMap<String,Object>();
        imageData.put("areaX", x*resolutionX/frameDivider);
        imageData.put("areaY",(frameDivider-y-1)*resolutionY/frameDivider);
        return imageData;
    }
}
