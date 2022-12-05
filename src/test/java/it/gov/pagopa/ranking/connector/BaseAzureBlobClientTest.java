package it.gov.pagopa.ranking.connector;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.BlobProperties;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.util.ReflectionUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;

abstract class BaseAzureBlobClientTest {
    protected AzureBlobClient blobClient;

    @BeforeEach
    void init() throws URISyntaxException, InvalidKeyException, StorageException {
        blobClient = builtBlobInstance();
    }

    protected abstract AzureBlobClient builtBlobInstance() throws URISyntaxException, InvalidKeyException, StorageException;

    @Test
    void uploadTest() {
        try {
            // Given
            InputStream testFile = new FileInputStream("README.md");
            String destination = "baseAzureBlobClientTest/README.md";

            mockClient(destination);

            // When Upload
            blobClient.uploadFile(testFile, destination, "text");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void mockClient(String destination) {
        try {
            Field clientField = ReflectionUtils.findField(OnboardingRankingBlobClientImpl.class, "blobContainer");
            Assertions.assertNotNull(clientField);
            clientField.setAccessible(true);

            CloudBlobContainer clientMock = Mockito.mock(CloudBlobContainer.class, Mockito.RETURNS_DEEP_STUBS);

            mockUploadFileOperation(destination, clientMock);
            clientField.set(blobClient, clientMock);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    protected static void mockUploadFileOperation(String destination, CloudBlobContainer clientMock) {
        try {
            CloudBlockBlob blockBlobMock = Mockito.mock(CloudBlockBlob.class, Mockito.RETURNS_DEEP_STUBS);

            Mockito.when(clientMock.getBlockBlobReference(destination)).thenReturn(blockBlobMock);
            Mockito.when(blockBlobMock.getProperties()).thenReturn(new BlobProperties());

        } catch (URISyntaxException | StorageException e) {
            throw new RuntimeException(e);
        }
    }
}
