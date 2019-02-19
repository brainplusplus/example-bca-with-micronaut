package bank.transaction.service.scheduler;

import bank.transaction.service.domain.AccessGrant;
import bank.transaction.service.domain.AccountStatement;
import bank.transaction.service.domain.AccountStatementDetail;
import bank.transaction.service.impl.BCAErrorHandler;
import bank.transaction.service.impl.BCATransactionInterceptor;
import bank.transaction.service.impl.BusinessBankingTemplate;
import bank.transaction.service.impl.Oauth2Template;
import bank.transaction.service.repository.Oauth2Operations;
import bank.transaction.service.service.AccountStatementService;
import bank.transaction.service.service.BcaService;
import bank.transaction.service.service.OrderService;
import io.micronaut.scheduling.annotation.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import javax.inject.Singleton;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;

@Singleton
public class TransactionCheckerJob {
    private static final Logger LOG = LoggerFactory.getLogger(TransactionCheckerJob.class);
    private final BcaService bcaService;
    private final Oauth2Template oauth2Template;
//    private final BCATransactionInterceptor bcaTransactionInterceptor;
    /*
    * Use this for Testing SANDBOX
    * */
//    private final String URL_BCA = "https://sandbox.bca.co.id";
//    private final String API_KEY = "a4a5403b-a49e-48e6-9213-c72ccfcf54eb";
//    private final String API_SECRET = "70ead5e3-15e1-431f-9774-a4d4efe9ba16";
//    private final String CLIENT_ID = "e571d7fd-9b5b-46e8-a835-2f5793c44611";
//    private final String CLIENT_SECRET = "cb711807-bd70-4069-a6ce-bb9739716cae";
//    private final String CORPORATE_ID = "BCAAPI2016";
//    private final String ACCOUNT_NUMBER = "0201245680";
    /*
    * Use this for Testing PRODUCTION
    * */
    private final String API_KEY = "114ee05a-2c94-4b4b-84cd-6957d156ed68";
    private final String API_SECRET = "6456f0b4-4a63-46a2-8e1f-5eac01fb64e5";
    private final String CLIENT_ID = "c3cd2bb1-3254-45a5-8bee-040d25a665da";
    private final String CLIENT_SECRET = "df0bc9c3-c466-4b15-90cf-29771c52ebe0";
    private final String CORPORATE_ID = "IBSDISTRIU";
    private final String ACCOUNT_NUMBER = "2774358888";

    protected AccessGrant accessGrant;
    private BusinessBankingTemplate businessBankingTemplate;
    private RestTemplate restTemplate;
    private final AccountStatementService accountStatementService;
    private final OrderService orderService;

    public TransactionCheckerJob(BcaService bcaService, Oauth2Template oauth2Template, RestTemplate restTemplate, AccountStatementService accountStatementService, OrderService orderService){
        this.bcaService = bcaService;
        this.oauth2Template = oauth2Template;
        this.restTemplate = restTemplate;
        this.accountStatementService = accountStatementService;
        this.orderService = orderService;
    }

    @Scheduled(fixedDelay = "10s")
    void executeEveryTen() throws Exception {
        LOG.info("Simple Job every 10 seconds :{}", new SimpleDateFormat("dd/M/yyyy hh:mm:ss").format(new Date()));
        LOG.info("JOB  :{}", new SimpleDateFormat("dd/M/yyyy hh:mm:ss").format(new Date()));

        Date fromDate = toDate(2019, 2,15);
        Date endDate = toDate(2019, 2, 15);

//        Date fromDate = toDate(2016, 9,1);
//        Date endDate = toDate(2016, 9, 1);

        BigDecimal abc = new BigDecimal(2900000);
        BusinessBankingTemplate businessBankingTemplate = new BusinessBankingTemplate(getRestTemplate());
//        LOG.info("\n\nHASIL businessBankingTemplate => {}", businessBankingTemplate.getStatement(CORPORATE_ID, ACCOUNT_NUMBER, fromDate, endDate));
        AccountStatement ac = accountStatementService.saveConditional(businessBankingTemplate.getStatement(CORPORATE_ID, ACCOUNT_NUMBER, fromDate, endDate));
        LOG.info("\n\n\n\n\n\nBARA BARA ==> {}",ac);
        for (AccountStatementDetail acd: ac.getAccountStatementDetailList()) {
            orderService.CheckToTokdis(abc);
        }


    }

    @Scheduled(fixedDelay = "10s", initialDelay = "5s")
    void executeEveryFourtyFive() {
//        orderService.CheckToTokdis();
        LOG.info("Simple Job every 45 seconds :{}", new SimpleDateFormat("dd/M/yyyy hh:mm:ss").format(new Date()));
    }

    @Scheduled(fixedDelay = "5s")
    void expiredPaymentChecking(){
        orderService.autoUpdatePaymentStatusIfExpired();
    }

    protected RestTemplate getRestTemplate() {

        RestTemplate restTemplate = new RestTemplate();

        Oauth2Operations oauth2Operations = new Oauth2Template();
        AccessGrant accessGrant = oauth2Operations.getToken(CLIENT_ID, CLIENT_SECRET);

        restTemplate.setInterceptors(Collections.singletonList(new BCATransactionInterceptor(accessGrant.getAccessToken(), API_KEY, API_SECRET)));
        restTemplate.setErrorHandler(new BCAErrorHandler());

        return restTemplate;
    }

    protected Date toDate(int year, int month, int day) {

        Calendar calendar = new GregorianCalendar(year, month - 1, day);
        return new Date(calendar.getTimeInMillis());
    }
}

//https://www.mkyong.com/java/how-to-send-http-request-getpost-in-java/
