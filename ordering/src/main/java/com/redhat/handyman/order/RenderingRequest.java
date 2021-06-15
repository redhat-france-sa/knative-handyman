package com.redhat.handyman.order;

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Simple bean representing a rendering request event.
 * The renderingId should be the original responseId from origin RenderingResponse.
 * @author laurent
 */
@RegisterForReflection
public class RenderingRequest {

   private String objectKey;
   private String renderingId;
   private int areaX;
   private int areaY;
   private int resolutionX;
   private int resolutionY;

   public RenderingRequest() {
   }

   public String getObjectKey() {
      return objectKey;
   }

   public void setObjectKey(String objectKey) {
      this.objectKey = objectKey;
   }

   public String getRenderingId() {
      return renderingId;
   }

   public void setRenderingId(String renderingId) {
      this.renderingId = renderingId;
   }

   public int getAreaX() {
      return areaX;
   }

   public void setAreaX(int areaX) {
      this.areaX = areaX;
   }

   public int getAreaY() {
      return areaY;
   }

   public void setAreaY(int areaY) {
      this.areaY = areaY;
   }

   public int getResolutionX() {
      return resolutionX;
   }

   public void setResolutionX(int resolutionX) {
      this.resolutionX = resolutionX;
   }

   public int getResolutionY() {
      return resolutionY;
   }

   public void setResolutionY(int resolutionY) {
      this.resolutionY = resolutionY;
   }
}
