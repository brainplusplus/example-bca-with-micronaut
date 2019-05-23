package bank.transaction.service.controller;

import bank.transaction.service.repository.ReceivePaymentRepository;
import bank.transaction.service.service.ReceivePaymentService;
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
    private ReceivePaymentRepository receivePaymentRepository;
    private Logger Log = LoggerFactory.getLogger(ReceivePaymentController.class);
    public ReceivePaymentController(ReceivePaymentRepository receivePaymentRepository){
        this.receivePaymentRepository = receivePaymentRepository;
    }

    @Post("/")
    public String index(@Body ReceivePaymentValidation receivePaymentValidation){
        Log.info("id ===== {}",receivePaymentValidation.toString());
        return receivePaymentRepository.COMPLETE_TRX(receivePaymentValidation.getInvoiceId());
    }
}
