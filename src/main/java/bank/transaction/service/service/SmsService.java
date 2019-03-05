package bank.transaction.service.service;

import io.micronaut.spring.tx.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.sql.DataSource;

@Singleton
public class SmsService {
    @Inject
    @Named("tokdis")
    DataSource dataSource; // "warehouse" will be injected

    @Inject
    @Named("maintokdis")
    DataSource dataSourceTokdisdev;

    private static final Logger LOG = LoggerFactory.getLogger(SmsService.class);
    private final String HOST_NAME= "http://13.250.223.74:3001";

    public SmsService(){

    }

    @Transactional
    public void sendNotificationServices(){

    }
}
