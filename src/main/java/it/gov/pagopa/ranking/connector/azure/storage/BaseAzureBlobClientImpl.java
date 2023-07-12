package it.gov.pagopa.ranking.connector.azure.storage;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;

@Slf4j
public abstract class BaseAzureBlobClientImpl implements AzureBlobClient{
    private final CloudBlobContainer blobContainer;

    protected BaseAzureBlobClientImpl(String storageConnectionString,
                                      String blobContainerName) throws URISyntaxException, InvalidKeyException, StorageException {
        final CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);
        this.blobContainer = storageAccount.createCloudBlobClient().getContainerReference(blobContainerName);
    }

    @Override
    public void uploadFile(Path file, String destination, String contentType) {
        try(InputStream signedFileStream = Files.newInputStream(file)) {
            this.uploadFile(
                    signedFileStream,
                    destination,
                    contentType
            );
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read file: %s".formatted(file), e);
        }
    }

    @Override
    public void uploadFile(InputStream file, String destination, String contentType) throws FileUploadException {
        log.info("Uploading file {} (contentType={}) into azure blob", destination, contentType);

        try {
            final CloudBlockBlob blob = blobContainer.getBlockBlobReference(destination);
            blob.getProperties().setContentType(contentType);
            blob.upload(file, file.available());
            log.info("Uploaded {}", destination);

        } catch (StorageException | URISyntaxException | IOException e) {
            throw new FileUploadException(e.getMessage(), e);
        }
    }
}
