package it.gov.pagopa.ranking.connector;

import com.microsoft.azure.storage.StorageException;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.Properties;

public class OnboardingRankingBlobClientTestIntegrated extends BaseAzureBlobClientTest {
    private final String connectionString;

    public OnboardingRankingBlobClientTestIntegrated() throws IOException {
        try(InputStream storageAccountPropertiesIS = new BufferedInputStream(new FileInputStream("src/test/resources/secrets/storageAccount.properties"))){
            Properties props = new Properties();
            props.load(storageAccountPropertiesIS);
            connectionString=props.getProperty("app.ranking-build-file.retrieve-initiative.blob-storage.string-connection");
        }
    }

    @Override
    protected AzureBlobClient builtBlobInstance() throws URISyntaxException, InvalidKeyException, StorageException {
        return new OnboardingRankingBlobClientImpl(connectionString, "ranking");
    }

    @Override
    protected void mockClient(String destination, boolean isKO){
        // Do Nothing
    }
}
