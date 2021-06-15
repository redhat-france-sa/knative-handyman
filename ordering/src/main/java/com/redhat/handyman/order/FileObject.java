package com.redhat.handyman.order;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class FileObject {
   private String key;
   private String ETag;
   private Long size;

   public FileObject() {
   }

   public String getKey() {
      return key;
   }

   public void setKey(String key) {
      this.key = key;
   }

   public String getETag() {
      return ETag;
   }

   public void setETag(String eTag) {
      this.ETag = eTag;
   }

   public Long getSize() {
      return size;
   }

   public void setSize(Long size) {
      this.size = size;
   }

   @Override
   public String toString() {
      return "FileObject{" +
            "key='" + key + '\'' +
            ", ETag='" + ETag + '\'' +
            ", size=" + size +
            '}';
   }
}
