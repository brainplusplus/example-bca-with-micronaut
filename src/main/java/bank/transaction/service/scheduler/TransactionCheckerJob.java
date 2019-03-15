package bank.transaction.service.scheduler;

import bank.transaction.service.domain.AccessGrant;
import bank.transaction.service.domain.AccountStatement;
import bank.transaction.service.domain.AccountStatementDetail;
import bank.transaction.service.impl.BCAErrorHandler;
import bank.transaction.service.impl.BCATransactionInterceptor;
import bank.transaction.service.impl.BusinessBankingTemplate;
import bank.transaction.service.impl.Oauth2Template;
import bank.transaction.service.repository.ExpeditionRepository;
import bank.transaction.service.repository.Oauth2Operations;
import bank.transaction.service.repository.Oauth2OperationsBNI;
import bank.transaction.service.repository.OrderServiceRepository;
import bank.transaction.service.service.AccountStatementService;
import bank.transaction.service.service.BcaService;
import io.micronaut.scheduling.annotation.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;
import javax.inject.Singleton;
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
    /**
    * Use this for Testing SANDBOX
    * */
//    private final String URL_BCA = "https://sandbox.bca.co.id";
//    private final String API_KEY = "a4a5403b-a49e-48e6-9213-c72ccfcf54eb";
//    private final String API_SECRET = "70ead5e3-15e1-431f-9774-a4d4efe9ba16";
//    private final String CLIENT_ID = "e571d7fd-9b5b-46e8-a835-2f5793c44611";
//    private final String CLIENT_SECRET = "cb711807-bd70-4069-a6ce-bb9739716cae";
//    private final String CORPORATE_ID = "BCAAPI2016";
//    private final String ACCOUNT_NUMBER = "0201245680";
    /**
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
    private final OrderServiceRepository orderServiceRepository;
    private final Oauth2OperationsBNI oauth2OperationsBNI;
    private final ExpeditionRepository expeditionRepository;

    public TransactionCheckerJob(ExpeditionRepository expeditionRepository, OrderServiceRepository orderServiceRepository, BcaService bcaService, Oauth2Template oauth2Template, Oauth2OperationsBNI oauth2OperationsBNI, RestTemplate restTemplate, AccountStatementService accountStatementService){
        this.bcaService = bcaService;
        this.oauth2Template = oauth2Template;
        this.restTemplate = restTemplate;
        this.accountStatementService = accountStatementService;
        this.oauth2OperationsBNI = oauth2OperationsBNI;
        this.orderServiceRepository = orderServiceRepository;
        this.expeditionRepository = expeditionRepository;
    }

    /**
     * Case No. 1 Reseller ->lebih dari 6 jam batas pembayaran
     * TODO Auto Check jika pembayaran expired -> if payment_status = 0 and payment_expired_at < now()
     * then payment_status = 0 and payment_verified_by = 0 and payment_verified_at = now() and is_paid = 0 and is_cancelled = 1
     * Then update stock
     *
     * */
    @Scheduled(fixedDelay = "270s", initialDelay = "5s")
    void expiredPaymentChecking(){
        LOG.info("------- Case at: expiredPaymentChecking");
        LOG.info(" ------------------------------ EXECUTE AT :{}", new SimpleDateFormat("dd/M/yyyy hh:mm:ss").format(new Date()));
        orderServiceRepository.autoUpdatePaymentStatusIfExpired();
        LOG.info(" ------------------------------ END AT :{}", new SimpleDateFormat("dd/M/yyyy hh:mm:ss").format(new Date()));
        LOG.info("\n-------------------------------------------------------------------------------");
    }

    /**
     * Case No. 2 -> Reseller sudah melakukan pembayaran -> Approve akan otomatsi berjalan dan transaksi akan otomatis diteruskan ke supplier
     * TODO update order sumarries ->payment_status = 1, payment_verified_by = 0, payment_verified_at = now(), is_paid = 1
     * TODO update order suppliers ->supplier_feedback_expired_AT = now()+1 DAY, order_status = 1
     * TODO Send Notification ONESIGNAL "Pesanan Berhasil dibayar" -> "Pembayaran pesananmu TDO/20190225/0000160 telah dikonfirmasi dan diteruskan ke penjual. Silahkan tunggu pesanan dikirim."
     * */
    @Scheduled(fixedDelay = "270s", initialDelay = "30s")
    void executeEveryTen() throws Exception {
        LOG.info("------- Case at: executeEveryTen");
        LOG.info(" ------------------------------ EXECUTE AT :{}", new SimpleDateFormat("dd/M/yyyy hh:mm:ss").format(new Date()));
//        LOG.info("JOB  :{}", new SimpleDateFormat("dd/M/yyyy hh:mm:ss").format(new Date()));
//        Date fromDate = toDate(2019, 2,15);
//        Date endDate = toDate(2019, 2, 15);

        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH) + 1; // Note: zero based!
        int day = now.get(Calendar.DAY_OF_MONTH);


        Date fromDate = toDate(year, month,day);
        Date endDate = toDate(year, month, day);
//
//        AccessGrant testGetTokenBNI = oauth2OperationsBNI.getToken("d78e500c-76c1-49e8-a4d8-41c5154b150e","ad0882f2-b9b4-46c2-beca-ff2946e4e1aa");
        /** THIS IS IMPORTANT */
        BusinessBankingTemplate businessBankingTemplate = new BusinessBankingTemplate(getRestTemplate());
        AccountStatement ac = accountStatementService.saveConditional(businessBankingTemplate.getStatement(CORPORATE_ID, ACCOUNT_NUMBER, fromDate, endDate));
        for (AccountStatementDetail acd: ac.getAccountStatementDetailList()) {
            orderServiceRepository.CheckToTokdis(acd.getAmount());
        }
//        LOG.info("\n\n\nGET TOKEN BNI --> {}",testGetTokenBNI.getAccessToken());
        LOG.info(" ------------------------------ END AT :{}", new SimpleDateFormat("dd/M/yyyy hh:mm:ss").format(new Date()));
        LOG.info("\n-------------------------------------------------------------------------------");

    }

    /**
     * Case No. 3 Reseller -> Dikirim -> Pesanan kaan otomatis pindah ke transaksi sampai
     * CronJob berjalan setiap 4 jam sekali
     * TODO update order supplier -> delivery_status = 1 , order_status = 5 and confirmed_expired_at = now()+2 DAYS and is_delivered = 1 and delivered_at now()
     * TODO Send Notification ONESIGNAL -> Pesanan Sampai -> "Pesananmu INV/20190225/00000005 telah sampai. Silahkan konfirmasi penerimaan pesananmu."
     * */
    @Scheduled(fixedDelay = "14400s", initialDelay = "60s")
    void executeEveryFourtyFive() throws Exception {
        LOG.info("------- Case at: executeEveryFourtyFive");
        LOG.info(" ------------------------------ EXECUTE AT :{}", new SimpleDateFormat("dd/M/yyyy hh:mm:ss").format(new Date()));
        expeditionRepository.CheckTracking();
        LOG.info(" ------------------------------ END AT :{}", new SimpleDateFormat("dd/M/yyyy hh:mm:ss").format(new Date()));
        LOG.info("\n-------------------------------------------------------------------------------");
    }

    /**
     * Case No. 4 Reseller-> "Reseller - Sampai" ->Pesanan akan otomatis pindah ke transaksi selesai
     * TODO Autocheck if confirmed_expired_at < now() and confirmed_at = null
     * TODO send ONESIGNAL notification -> Pesanan Selesai -> "Pesananmu INV/20190225/00000005 telah selesai. Silahkan berikan penilaian pesananmu."
     * then update order_status = 6 and confirmed_at now()
     * + update saldo ke beranda supplier
     * */
    @Scheduled(fixedDelay = "270s", initialDelay = "90s")
    void executeUpdateTransactionDone(){
        LOG.info("------- Case at: executeUpdateTransactionDone");
        LOG.info(" ------------------------------ EXECUTE AT :{}", new SimpleDateFormat("dd/M/yyyy hh:mm:ss").format(new Date()));
        orderServiceRepository.updateOrderStatusToDone();
        LOG.info(" ------------------------------ END AT :{}", new SimpleDateFormat("dd/M/yyyy hh:mm:ss").format(new Date()));
        LOG.info("\n-------------------------------------------------------------------------------");
    }

    /**
     * CASE No. 1 Supplier -> Supplier -> Transaksi - Pesanan BAru jika Supplier tidak merespon pesanan tersebut melebihi 1x24jam
     * TODO autocheck if supplier_feeback_expired_at < now() and supplier_feedback_at = null and order_status = 1
     * then update is_rejected = 1 and order_status = 2 and supplier_feedback_at = now()
     * - balikin stock
     * - balikin saldo ke brankas
     * */
    @Scheduled(fixedDelay = "270s", initialDelay = "120s")
    void executeUpdateStatusTransactionIfSupplierNotRespond(){
        LOG.info("------- Case at: executeUpdateStatusTransactionIfSupplierNotRespond");
        LOG.info(" ------------------------------ EXECUTE AT :{}", new SimpleDateFormat("dd/M/yyyy hh:mm:ss").format(new Date()));
        orderServiceRepository.updateOrderStatusRejected();
        LOG.info("------------------------------ END AT :{}", new SimpleDateFormat("dd/M/yyyy hh:mm:ss").format(new Date()));
        LOG.info("\n-------------------------------------------------------------------------------");
    }

    /**
     * Case No.2 -> Supplier-> Pesanan akan otomatis dibatalkan, Supplier tidak meingin pesanan lebih dari 1x24Jam
     * TODO update order summaries -> is_rejected = 1 and order_status = 2 pesanan ditolak
     * - Balikin stock
     * - balikin saldo reseller brankas
     * */
    @Scheduled(fixedDelay = "270s", initialDelay = "150s")
    void executeUpdateStatusTransactionIfSupplierNotSentTheOrder(){
        LOG.info("------- Case at: executeUpdateStatusTransactionIfSupplierNotSentTheOrder");
        LOG.info("------------------------------ EXECUTE AT :{}", new SimpleDateFormat("dd/M/yyyy hh:mm:ss").format(new Date()));
        orderServiceRepository.UpdateIsRejectedIfSupplierNotSentTheOrder();
        LOG.info("------------------------------ END AT :{}", new SimpleDateFormat("dd/M/yyyy hh:mm:ss").format(new Date()));
        LOG.info("\n-------------------------------------------------------------------------------");

    }
//    /**
//     * Case No.3 ->Transaksi Sampai
//     * order status = 6
//     * confirm_at today
//     * ini sama dengan case reseller yang nomor 4
//     * TODO update order supplier to orderStatus = 6 and confirmed_at = now() if confirmed_at = null and confirmed_expired_at < now()
//     */
//    @Scheduled(fixedDelay = "270s", initialDelay = "180s")
//    void executeUpdateStatusToDoneifConfirmedExpiredMoreThanToday(){
//        LOG.info("------- Case at: executeUpdateStatusToDoneifConfirmedExpiredMoreThanToday");
//        LOG.info("------------------------------ EXECUTE AT :{}", new SimpleDateFormat("dd/M/yyyy hh:mm:ss").format(new Date()));
//        orderService.UpdateIsRejectedIfSupplierNotSentTheOrder();
//        LOG.info("------------------------------ END AT :{}", new SimpleDateFormat("dd/M/yyyy hh:mm:ss").format(new Date()));
//        LOG.info("\n-------------------------------------------------------------------------------");
//    }

    /**
     * Case Reminder
     * TODO Notifikasi Pesanan Menunggu Pembayaran
     * TODO Send Notification ONESIGNAL -> "Segera lakukan pembayaran sebesar Rp 1.234.000 untuk pesananmu TDO/20190225/0000160 sebelum 06-02-2019 22.32 untuk menghindari pembatalan."
     * */
    @Scheduled(fixedDelay = "5s")
    void executeForReminder(){
        LOG.info("\n\n\nCheck REMINDER --> ");
        orderServiceRepository.checkForReminder();
        LOG.info("\n\n\nDate Format --> {}",new SimpleDateFormat("EEEE, dd MMMM yyyy").format(new Date()));
        LOG.info("\n\n\nDate Format --> {}",new SimpleDateFormat("hh.mm").format(new Date()));
    }

    /**
     * TODO Case Supplier - ONESIGNAL
     * TODO "notifikasi segera kirim barang, input no. Resi"
     *
     * */
    @Scheduled(fixedDelay = "5s")
    void executeNotificationMustSendItem(){
        LOG.info("\n\n\nCase Supplier --> ");
        orderServiceRepository.sentNotifMustSentItem();
        LOG.info("\n\n\nDate Format --> {}",new SimpleDateFormat("EEEE, dd MMMM yyyy").format(new Date()));
        LOG.info("\n\n\nDate Format --> {}",new SimpleDateFormat("hh.mm").format(new Date()));
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
