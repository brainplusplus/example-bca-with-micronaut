package bank.transaction.service.service;

import bank.transaction.service.impl.Oauth2Template;
import com.nimbusds.jose.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.validation.constraints.NotNull;

public class BcaService {
    private static final Logger LOG = LoggerFactory.getLogger(BcaService.class);
    private final String URL_BCA = "https://sandbox.bca.co.id/api/oauth/token";
    private static String BCA_AUTHORIZATION_HEADER = "Basic ";
    private final String API_KEY = "a4a5403b-a49e-48e6-9213-c72ccfcf54eb";
    private final String API_SECRET = "70ead5e3-15e1-431f-9774-a4d4efe9ba16";
    private final String CLIENT_ID = "e571d7fd-9b5b-46e8-a835-2f5793c44611";
    private final String CLIENT_SECRET = "cb711807-bd70-4069-a6ce-bb9739716cae";
    private final String CONTENT_TYPE = "application/x-www-form-urlencoded";
    private final String CORPORATE_ID = "BCAAPI2016";
    protected final Oauth2Template oauth2Template;

    public BcaService(Oauth2Template oauth2Template){
        this.oauth2Template = oauth2Template;
    }

    public void authenticate(){
        LOG.info("Hasil encode : {}", oauth2Template.getToken(CLIENT_ID,CLIENT_SECRET));
    }

    private String encodeBase64(@NotNull String firstWord,@NotNull String clientId, @NotNull String clientSecret){
        Base64 encode = Base64.encode(clientId+":"+clientSecret);

        LOG.info("Hasil encode : {}", encode);

        return firstWord+Base64.encode(clientId+":"+clientSecret).toString();
    }

}
