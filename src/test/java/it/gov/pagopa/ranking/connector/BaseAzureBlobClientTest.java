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

            mockClient(destination, false);

            // When
            blobClient.uploadFile(testFile, destination, "text");
        } catch (IOException | URISyntaxException | StorageException | IllegalAccessException e) {
            Assertions.fail();
        }
    }

    protected void mockClient(String destination, boolean isKO) throws URISyntaxException, StorageException, IllegalAccessException {

        Field clientField = ReflectionUtils.findField(OnboardingRankingBlobClientImpl.class, "blobContainer");
        Assertions.assertNotNull(clientField);
        clientField.setAccessible(true);

        CloudBlobContainer clientMock = Mockito.mock(CloudBlobContainer.class, Mockito.RETURNS_DEEP_STUBS);

        mockUploadFileOperation(destination, clientMock, isKO);
        clientField.set(blobClient, clientMock);
    }

    protected static void mockUploadFileOperation(String destination, CloudBlobContainer clientMock, boolean isKO) throws URISyntaxException, StorageException {

        CloudBlockBlob blockBlobMock = Mockito.mock(CloudBlockBlob.class, Mockito.RETURNS_DEEP_STUBS);

        if (isKO) {
            Mockito.when(clientMock.getBlockBlobReference(destination)).thenThrow(StorageException.class);
        } else {
            Mockito.when(clientMock.getBlockBlobReference(destination)).thenReturn(blockBlobMock);
            Mockito.when(blockBlobMock.getProperties()).thenReturn(new BlobProperties());
        }
    }
}
