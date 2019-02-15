package bank.transaction.service.usebean;

import bank.transaction.service.impl.BusinessBankingTemplate;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import org.springframework.web.client.RestTemplate;

import javax.inject.Singleton;

@Factory
public class RestTemplateFactory {

    @Bean
    @Singleton
    public RestTemplate v8Engine() {
      return new RestTemplate();
    }
}
