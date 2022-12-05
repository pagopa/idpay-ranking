package it.gov.pagopa.ranking.connector;

import com.microsoft.azure.storage.StorageException;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;

class InitiativeRankingBlobClientTest extends BaseAzureBlobClientTest {
    protected AzureBlobClient builtBlobInstance() throws URISyntaxException, InvalidKeyException, StorageException {
        return new InitiativeRankingBlobClientImpl("UseDevelopmentStorage=true;", "test");
    }

    @Test
    void uploadKOTest(){
        try {
            // Given
            InputStream testFile = new FileInputStream("README.md");
            String destination = "baseAzureBlobClientTest/README.md";

            mockClient(destination, true);

            // When
            blobClient.uploadFile(testFile, destination, "text");
        } catch (URISyntaxException | StorageException | IllegalAccessException | FileNotFoundException | FileUploadException e) {
            Assertions.assertTrue(e instanceof FileUploadException);
        }
    }
}