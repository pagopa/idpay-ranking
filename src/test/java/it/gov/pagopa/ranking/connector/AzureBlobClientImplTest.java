package it.gov.pagopa.ranking.connector;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.BlobProperties;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;

class AzureBlobClientImplTest {

    @Test
    void uploadFile_ok() throws IOException, StorageException, URISyntaxException, NoSuchFieldException, IllegalAccessException, InvalidKeyException {
        // Given
        AzureBlobClientImpl blobClient = new AzureBlobClientImpl("UseDevelopmentStorage=true;", "$web");
        CloudBlockBlob blockBlobMock = Mockito.mock(CloudBlockBlob.class);
        Mockito.when(blockBlobMock.getProperties())
                .thenReturn(new BlobProperties());
        Mockito.doNothing().
                when(blockBlobMock).upload(Mockito.any(), Mockito.anyByte());
        CloudBlobContainer blobContainerMock = Mockito.mock(CloudBlobContainer.class);
        Mockito.when(blobContainerMock.getBlockBlobReference("file-blob.csv.p7m"))
                .thenReturn(blockBlobMock);
        CloudBlobClient blobClientMock = Mockito.mock(CloudBlobClient.class);
        Mockito.when(blobClientMock.getContainerReference("$web"))
                .thenReturn(blobContainerMock);
        mockCloudBlobClient(blobClient, blobClientMock);
        InputStream resource = new ClassPathResource("file-blob.csv.p7m")
                .getInputStream();
        // when
        Executable executable = () -> blobClient.uploadFile(resource, "file-blob.csv.p7m", "application/p7m");
        // then
        Assertions.assertDoesNotThrow(executable);
    }

    private void mockCloudBlobClient(AzureBlobClientImpl blobClient, CloudBlobClient blobClientMock) throws NoSuchFieldException, IllegalAccessException {
        Field field = AzureBlobClientImpl.class.getDeclaredField("blobClient");
        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(blobClient, blobClientMock);
    }
}