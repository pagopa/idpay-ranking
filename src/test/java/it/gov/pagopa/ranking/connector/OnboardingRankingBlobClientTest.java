package it.gov.pagopa.ranking.connector;

import com.microsoft.azure.storage.StorageException;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;

class OnboardingRankingBlobClientTest extends BaseAzureBlobClientTest{
    protected AzureBlobClient builtBlobInstance() throws URISyntaxException, InvalidKeyException, StorageException {
        try {
            return new OnboardingRankingBlobClientImpl("UseDevelopmentStorage=true;", "test");
        } catch (URISyntaxException | InvalidKeyException | StorageException e) {
            throw new RuntimeException(e);
        }
    }
}