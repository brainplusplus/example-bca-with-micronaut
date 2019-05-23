//package bank.transaction.service.controller;
//
//import bank.transaction.service.Common;
//import bank.transaction.service.domain.AccessGrant;
//import bank.transaction.service.domain.AccountStatement;
//import bank.transaction.service.domain.AccountStatementDetail;
//import bank.transaction.service.impl.BCAErrorHandler;
//import bank.transaction.service.impl.BCATransactionInterceptor;
//import bank.transaction.service.impl.BusinessBankingTemplate;
//import bank.transaction.service.impl.Oauth2Template;
//import bank.transaction.service.repository.Oauth2Operations;
//import bank.transaction.service.service.AccountStatementService;
//import io.micronaut.http.annotation.Controller;
//import io.micronaut.http.annotation.Get;
//import io.micronaut.http.annotation.Post;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.web.client.RestTemplate;
//
//import javax.validation.constraints.NotBlank;
//import javax.validation.constraints.NotNull;
//import java.util.Calendar;
//import java.util.Collections;
//import java.util.Date;
//import java.util.GregorianCalendar;
//
//@Controller("/cek-statement")
//public class CheckStatement {
//    private static Logger LOG = LoggerFactory.getLogger(CheckStatement.class);
//    private final AccountStatementService accountStatementService;
//    private RestTemplate restTemplate;
//    private final Common common;
//
//    public CheckStatement(AccountStatementService accountStatementService, RestTemplate restTemplate, Common common){
//        this.accountStatementService = accountStatementService;
//        this.restTemplate = restTemplate;
//        this.common = common;
//    }
//
//    @Post("/")
//    public String checkstatement(@NotNull int month, @NotNull int day){
//        AccountStatement ac = null;
//        Calendar now = Calendar.getInstance();
//
//
//        Date fromDate = toDate(2019, month,day);
//        Date endDate = toDate(2019, month, day);
//
////        Date fromDate = toDate(year, month,day);
////        Date endDate = toDate(year, month, day);
//
////        AccessGrant testGetTokenBNI = oauth2OperationsBNI.getToken("d78e500c-76c1-49e8-a4d8-41c5154b150e","ad0882f2-b9b4-46c2-beca-ff2946e4e1aa");
//        /** THIS IS IMPORTANT */
//        BusinessBankingTemplate businessBankingTemplate = new BusinessBankingTemplate(getRestTemplate());
//        try {
//            ac = accountStatementService.saveConditional(businessBankingTemplate.getStatement(common.BCA_CORPORATE_ID,common.BCA_ACCOUNT_NUMBER, fromDate, endDate));
//            LOG.error("----------- : {}\n\n",ac.toString());
//            for (AccountStatementDetail acd: ac.getAccountStatementDetailList()) {
////                orderServiceRepository.CheckToTokdis(acd.getAmount());
//            }
//        }
//        catch (Exception ex){
//            LOG.error("----------- NO TRANSACTION!!!!");
//        }
//
//        return ac.getAccountStatementDetailList().toString();
//    }
//
//    protected RestTemplate getRestTemplate() {
//        RestTemplate restTemplate = new RestTemplate();
//        Oauth2Operations oauth2Operations = new Oauth2Template();
//        AccessGrant accessGrant = oauth2Operations.getToken(common.BCA_CLIENT_ID, common.BCA_CLIENT_SECRET);
//        restTemplate.setInterceptors(Collections.singletonList(new BCATransactionInterceptor(accessGrant.getAccessToken(), common.BCA_API_KEY, common.BCA_API_SECRET)));
//        restTemplate.setErrorHandler(new BCAErrorHandler());
//        return restTemplate;
//    }
//
//    protected Date toDate(int year, int month, int day) {
//        Calendar calendar = new GregorianCalendar(year, month - 1, day);
//        return new Date(calendar.getTimeInMillis());
//    }
//
//}
