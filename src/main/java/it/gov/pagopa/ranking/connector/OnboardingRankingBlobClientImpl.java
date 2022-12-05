package it.gov.pagopa.ranking.connector;

import com.microsoft.azure.storage.StorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;

@Slf4j
@Service
public class OnboardingRankingBlobClientImpl extends BaseAzureBlobClientImpl implements OnboardingRankingBlobClient {

    public OnboardingRankingBlobClientImpl(
            @Value("${app.ranking-build-file.retrieve-initiative.blob-storage.string-connection}") String storageConnectionString,
            @Value("${app.ranking-build-file.retrieve-initiative.blob-storage.blob-container-name}") String blobContainerName)
            throws URISyntaxException, InvalidKeyException, StorageException {
        super(storageConnectionString, blobContainerName);
    }
}
