package it.gov.pagopa.ranking.connector;

import org.apache.tomcat.util.http.fileupload.FileUploadException;

import java.io.InputStream;

public interface AzureBlobClient {
    void uploadFile(InputStream file, String destination, String contentType) throws FileUploadException;

}
