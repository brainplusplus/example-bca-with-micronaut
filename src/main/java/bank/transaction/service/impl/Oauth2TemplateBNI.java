package bank.transaction.service.impl;

import bank.transaction.service.domain.AccessGrant;
import bank.transaction.service.repository.Oauth2OperationsBNI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.inject.Singleton;
import java.util.Base64;

@Singleton
public class Oauth2TemplateBNI extends AbstractBNIOperations implements Oauth2OperationsBNI {
    private static final Logger LOG = LoggerFactory.getLogger(Oauth2TemplateBNI.class);
    public Oauth2TemplateBNI() {
        this.restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new BCAErrorHandler());
    }

    @Override
    public AccessGrant getToken(String clientId, String clientSecret) {
        LOG.info("\n\n\nAuthorization -> {}",getAuthorization(clientId, clientSecret));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Authorization", getAuthorization(clientId, clientSecret));

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        return restTemplate.postForObject(buildUrl("/api/oauth/token"), request, AccessGrant.class);
    }

    private String getAuthorization(String clientId, String clientSecret) {

        return "Basic " + Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
    }
}
