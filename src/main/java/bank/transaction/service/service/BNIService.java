package bank.transaction.service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;

@Singleton
public class BNIService {
    private static final Logger LOG = LoggerFactory.getLogger(BNIService.class);
    private final String URL_BCA = "https://digitalservices.bni.co.idd";
//    private final String API_KEY = "a4a5403b-a49e-48e6-9213-c72ccfcf54eb";
//    private final String API_SECRET = "70ead5e3-15e1-431f-9774-a4d4efe9ba16";
    private final String CLIENT_ID = "d78e500c-76c1-49e8-a4d8-41c5154b150e";
    private final String CLIENT_SECRET = "ad0882f2-b9b4-46c2-beca-ff2946e4e1aa";
    private final String CONTENT_TYPE = "application/x-www-form-urlencoded";
//    private final String CORPORATE_ID = "BCAAPI2016";
//    private final String ACCOUNT_NUMBER = "0201245680";

    public BNIService(){

    }

    public void getToken(){

    }
}
