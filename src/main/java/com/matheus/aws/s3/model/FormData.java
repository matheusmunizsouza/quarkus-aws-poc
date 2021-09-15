package com.matheus.aws.s3.model;

import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.multipart.FileUpload;

public class FormData {

  @FormParam("file")
  @PartType(MediaType.APPLICATION_OCTET_STREAM)
  private FileUpload data;

  public FileUpload getData() {
    return data;
  }
}
