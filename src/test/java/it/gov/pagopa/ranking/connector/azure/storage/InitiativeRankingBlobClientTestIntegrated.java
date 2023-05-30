package it.gov.pagopa.ranking.connector.azure.storage;

import com.microsoft.azure.storage.StorageException;
import it.gov.pagopa.common.mongo.MongoTestIntegrated;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.Properties;

/**
 * See confluence page: <a href="https://pagopa.atlassian.net/wiki/spaces/IDPAY/pages/615974424/Secrets+UnitTests">Secrets for UnitTests</a>
 */
@SuppressWarnings({"squid:S3577", "NewClassNamingConvention"}) // suppressing class name not match alert: we are not using the Test suffix in order to let not execute this test by default maven configuration because it depends on properties not pushable. See
@MongoTestIntegrated
class InitiativeRankingBlobClientTestIntegrated extends BaseAzureBlobClientTest {
    private final String connectionString;

    public InitiativeRankingBlobClientTestIntegrated() throws IOException {
        try(InputStream storageAccountPropertiesIS = new BufferedInputStream(new FileInputStream("src/test/resources/secrets/storageAccount.properties"))){
            Properties props = new Properties();
            props.load(storageAccountPropertiesIS);
            connectionString=props.getProperty("app.ranking-build-file.retrieve-initiative.blob-storage.string-connection");
        }
    }

    @Override
    protected AzureBlobClient builtBlobInstance() throws URISyntaxException, InvalidKeyException, StorageException {
        return new InitiativeRankingBlobClientImpl(connectionString, "ranking");
    }

    @Override
    protected void mockClient(String destination, boolean isKO){
        // Do Nothing
    }
}
