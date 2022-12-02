package it.gov.pagopa.ranking.connector;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;

@Slf4j
@Service
public class AzureBlobClientImpl implements AzureBlobClient {
    private final CloudBlobClient blobClient;
    private final String blobContainerName;

    public AzureBlobClientImpl(@Value("${app.ranking-build-file.retrieve-initiative.blob-storage.string-connection}") String storageConnectionString,
                               @Value("${app.ranking-build-file.retrieve-initiative.blob-storage.blob-container-name}") String blobContainerName)
            throws URISyntaxException, InvalidKeyException {

        log.info("AzureBlobClient storageConnectionString = {}, containerReference = {}", storageConnectionString, blobContainerName);

        final CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);
        this.blobClient = storageAccount.createCloudBlobClient();
        this.blobContainerName = blobContainerName;
    }

    @Override
    public void uploadFile(InputStream file, String fileName, String contentType) throws FileUploadException {
        log.info("Uploading file {} (contentType={}) into azure blob", fileName, contentType);

        try {
            final CloudBlobContainer blobContainer = blobClient.getContainerReference(blobContainerName);
            final CloudBlockBlob blob = blobContainer.getBlockBlobReference(fileName);
            blob.getProperties().setContentType(contentType);
            blob.upload(file, file.available());
            log.info("Uploaded {}", fileName);

        } catch (StorageException | URISyntaxException | IOException e) {
            throw new FileUploadException(e);
        }
    }
}
