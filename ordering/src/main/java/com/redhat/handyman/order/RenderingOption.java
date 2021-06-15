package com.redhat.handyman.order;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.Objects;

@RegisterForReflection
public class RenderingOption {

   private String name;
   private int cost;
   private int samples;
   private int frameDividers;
   private int resolutionX;
   private int resolutionY;

   public RenderingOption() {
   }

   public RenderingOption(String name, int cost, int samples, int frameDividers, int resolutionX, int resolutionY) {
      this.name = name;
      this.cost = cost;
      this.samples = samples;
      this.frameDividers = frameDividers;
      this.resolutionX = resolutionX;
      this.resolutionY = resolutionY;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public int getCost() {
      return cost;
   }

   public void setCost(int cost) {
      this.cost = cost;
   }

   public int getSamples() {
      return samples;
   }

   public void setSamples(int samples) {
      this.samples = samples;
   }

   public int getFrameDividers() {
      return frameDividers;
   }

   public void setFrameDividers(int frameDividers) {
      this.frameDividers = frameDividers;
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

   @Override
   public String toString() {
      return "RenderingOption{" +
            "name='" + name + '\'' +
            ", cost=" + cost +
            ", samples=" + samples +
            ", frameDividers=" + frameDividers +
            ", resolutionX=" + resolutionX +
            ", resolutionY=" + resolutionY +
            '}';
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      RenderingOption that = (RenderingOption) o;
      return cost == that.cost && samples == that.samples && frameDividers == that.frameDividers
            && resolutionX == that.resolutionX && resolutionY == that.resolutionY
            && Objects.equals(name, that.name);
   }

   @Override
   public int hashCode() {
      return Objects.hash(name, cost, samples, frameDividers, resolutionX, resolutionY);
   }
}
