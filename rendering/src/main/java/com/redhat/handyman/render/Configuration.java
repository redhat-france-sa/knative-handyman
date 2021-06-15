package com.redhat.handyman.render;

import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

@ApplicationScoped
public class Configuration {

   @Named("s3Client")
   public S3Client awsS3Client() {
      return S3Client.builder()
            .credentialsProvider(new AwsCredentialsProvider() {
               @Override
               public AwsCredentials resolveCredentials() {
                  return new AwsCredentials() {
                     @Override
                     public String accessKeyId() {
                        return "AKIA2HTUU7RLXSQ4MI3L";
                     }

                     @Override
                     public String secretAccessKey() {
                        return "iluSMfpAeEqlBCfkUap+NoRsZC2Yr7KrqFKoTToP";
                     }
                  };
               }
            })
            .region(Region.EU_WEST_3).build();
   }
}
