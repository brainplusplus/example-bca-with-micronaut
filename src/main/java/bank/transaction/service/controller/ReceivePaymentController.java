package bank.transaction.service.controller;

import bank.transaction.service.repository.OrderServiceRepository;
import bank.transaction.service.service.OrderService;
import bank.transaction.service.validation.ReceivePaymentValidation;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import java.util.List;

@Controller("/api/receive-payment")
public class ReceivePaymentController {
    private OrderServiceRepository orderServiceRepository;
    private Logger Log = LoggerFactory.getLogger(ReceivePaymentController.class);
    public ReceivePaymentController(OrderServiceRepository orderServiceRepository){
        this.orderServiceRepository = orderServiceRepository;
    }

    @Post("/")
    public String index(@Body ReceivePaymentValidation receivePaymentValidation){
        return orderServiceRepository.COMPLETE_TRX(receivePaymentValidation.getInvoiceId());
    }
}
