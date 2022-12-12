package it.gov.pagopa.ranking.connector.azure.storage;

import com.microsoft.azure.storage.StorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;

@Slf4j
@Service
public class InitiativeRankingBlobClientImpl extends BaseAzureBlobClientImpl implements InitiativeRankingBlobClient {

    public InitiativeRankingBlobClientImpl(
            @Value("${app.ranking-build-file.retrieve-initiative.blob-storage.string-connection}") String storageConnectionString,
            @Value("${app.ranking-build-file.retrieve-initiative.blob-storage.blob-container-name}") String blobContainerName)
            throws URISyntaxException, InvalidKeyException, StorageException {
        super(storageConnectionString, blobContainerName);
    }
}
