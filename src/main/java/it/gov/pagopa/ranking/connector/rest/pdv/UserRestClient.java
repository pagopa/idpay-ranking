package it.gov.pagopa.ranking.connector.rest.pdv;

import it.gov.pagopa.ranking.dto.pdv.UserInfoPDV;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;

@FeignClient(name = "pdv", url = "${app.pdv.base-url}")
public interface UserRestClient {

    @GetMapping(value = "/tokens/{token}/pii", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    UserInfoPDV getPii(@PathVariable("token") String userId, @RequestHeader("x-api-key") String apiKey);
}
