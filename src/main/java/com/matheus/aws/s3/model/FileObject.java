package com.matheus.aws.s3.model;

import software.amazon.awssdk.services.s3.model.S3Object;

public class FileObject {

  private String objectKey;
  private Long size;

  private FileObject() {
  }

  private FileObject(String objectKey, Long size) {
    this.objectKey = objectKey;
    this.size = size;
  }

  public static FileObject from(S3Object s3Object) {
    if (s3Object == null) {
      throw new IllegalArgumentException("S3Objet must not be null");
    }
    return new FileObject(s3Object.key(), s3Object.size());
  }

  public String getObjectKey() {
    return objectKey;
  }

  public Long getSize() {
    return size;
  }
}
