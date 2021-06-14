package com.redhat.handyman.order;

public class Order {

   private FileObject fileObject;
   private RenderingOption option;

   public Order() {
   }

   public FileObject getFileObject() {
      return fileObject;
   }

   public void setFileObject(FileObject fileObject) {
      this.fileObject = fileObject;
   }

   public RenderingOption getOption() {
      return option;
   }

   public void setOption(RenderingOption option) {
      this.option = option;
   }
}
