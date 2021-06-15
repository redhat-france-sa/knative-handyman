package com.redhat.handyman.order;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.Date;

/**
 * Simple bean representing a rendering response.
 * @author laurent
 */
@RegisterForReflection
public class RenderingResponse {

   private String responseId;
   private Date createdOn;
   private RenderingOption chosenOption;

   public RenderingResponse() {
   }

   public String getResponseId() {
      return responseId;
   }

   public void setResponseId(String responseId) {
      this.responseId = responseId;
   }

   public Date getCreatedOn() {
      return createdOn;
   }

   public void setCreatedOn(Date createdOn) {
      this.createdOn = createdOn;
   }

   public RenderingOption getChosenOption() {
      return chosenOption;
   }

   public void setChosenOption(RenderingOption chosenOption) {
      this.chosenOption = chosenOption;
   }
}
