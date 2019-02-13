package bank.transaction.service.scheduler;

import bank.transaction.service.impl.Oauth2Template;
import bank.transaction.service.repository.Oauth2Operations;
import bank.transaction.service.service.BcaService;
import io.micronaut.scheduling.annotation.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.text.SimpleDateFormat;
import java.util.Date;

@Singleton
public class TransactionCheckerJob {
    private static final Logger LOG = LoggerFactory.getLogger(TransactionCheckerJob.class);
    private final Oauth2Template oauth2Template;
    private final String URL_BCA = "https://sandbox.bca.co.id/api/oauth/token";
    private final String BCA_AUTHORIZATION_HEADER = "Basic ";
    private final String API_KEY = "a4a5403b-a49e-48e6-9213-c72ccfcf54eb";
    private final String API_SECRET = "70ead5e3-15e1-431f-9774-a4d4efe9ba16";
    private final String CLIENT_ID = "e571d7fd-9b5b-46e8-a835-2f5793c44611";
    private final String CLIENT_SECRET = "cb711807-bd70-4069-a6ce-bb9739716cae";
    private final String CONTENT_TYPE = "application/x-www-form-urlencoded";
    private final String CORPORATE_ID = "BCAAPI2016";

    public TransactionCheckerJob(Oauth2Template oauth2Template){
        this.oauth2Template = oauth2Template;
    }

    @Scheduled(fixedDelay = "10s")
    void executeEveryTen() {
        LOG.info("Simple Job every 10 seconds :{}", new SimpleDateFormat("dd/M/yyyy hh:mm:ss").format(new Date()));

        oauth2Template.getToken(CLIENT_ID,CLIENT_SECRET);
    }

    @Scheduled(fixedDelay = "45s", initialDelay = "5s")
    void executeEveryFourtyFive() {
        LOG.info("Simple Job every 45 seconds :{}", new SimpleDateFormat("dd/M/yyyy hh:mm:ss").format(new Date()));
    }
}

//https://www.mkyong.com/java/how-to-send-http-request-getpost-in-java/
