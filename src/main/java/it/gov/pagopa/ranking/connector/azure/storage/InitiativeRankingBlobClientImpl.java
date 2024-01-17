package it.gov.pagopa.ranking.connector.azure.storage;

import it.gov.pagopa.common.azure.storage.AzureBlobClientImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class InitiativeRankingBlobClientImpl extends AzureBlobClientImpl implements InitiativeRankingBlobClient {

    public InitiativeRankingBlobClientImpl(
            @Value("${app.ranking-build-file.retrieve-initiative.blob-storage.string-connection}") String storageConnectionString,
            @Value("${app.ranking-build-file.retrieve-initiative.blob-storage.blob-container-name}") String blobContainerName) {
        super(storageConnectionString, blobContainerName);
    }
}
