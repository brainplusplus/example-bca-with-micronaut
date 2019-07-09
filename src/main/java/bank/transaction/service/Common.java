package bank.transaction.service;

import javax.inject.Singleton;
import java.text.MessageFormat;

@Singleton
public class Common {
    public final String ALGORITM_HS256 = "HS256";
    public final String FORMAT_TYPE = "JWT";
    public final String HMAC_SHA256 = "HmacSHA256";
    public final String ACCESS_TOKEN = "access_token";

    /**
     * TODO Access BNI Server
     * */
    public final String BNI_CLIENT_ID = "IDBNIdG9rb2Rpc3RyaWJ1dG9y";
    public final String BNI_API_KEY = "bffc4b08-bebb-42eb-8e8d-e16bc0a3ae7d";
    public final String BNI_API_SECRET = "3643ab9a-e460-4393-8fad-2e9bfcee78a8";
    public final String BNI_HEADER_X_API_KEY = "x-api-key";
    public final String BNI_PATH_GET_PAYMENT_STATUS = "/H2H/v2/getpaymentstatus";
    public final String BNI_ACCESS_TOKEN_CLIENT_ID = "d78e500c-76c1-49e8-a4d8-41c5154b150e";
    public final String BNI_ACCESS_TOKEN_CLIENT_SECRET = "ad0882f2-b9b4-46c2-beca-ff2946e4e1aa";




//    public final String BCA_URL = "https://sandbox.bca.co.id";
//    public final String BCA_API_KEY = "a4a5403b-a49e-48e6-9213-c72ccfcf54eb";
//    public final String BCA_API_SECRET = "70ead5e3-15e1-431f-9774-a4d4efe9ba16";
//    public final String BCA_CLIENT_ID = "e571d7fd-9b5b-46e8-a835-2f5793c44611";
//    public final String BCA_CLIENT_SECRET = "cb711807-bd70-4069-a6ce-bb9739716cae";
//    public final String BCA_CORPORATE_ID = "BCAAPI2016";
//    public final String BCA_ACCOUNT_NUMBER = "0201245680";


    /**
     * Notification Server URL
     * */



}
