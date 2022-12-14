package it.gov.pagopa.ranking.connector.azure.storage;

import org.apache.tomcat.util.http.fileupload.FileUploadException;

import java.io.InputStream;
import java.nio.file.Path;

public interface AzureBlobClient {

    void uploadFile(Path file, String destination, String contentType);
    void uploadFile(InputStream file, String destination, String contentType) throws FileUploadException;

}
