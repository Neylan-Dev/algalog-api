package com.neylandev.delivery.domain.producer;

import com.neylandev.delivery.domain.dto.DeliveryEmailDto;
import com.neylandev.delivery.domain.utils.Constants;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeliverySendEmailProducer {

    @Produce(Constants.DIRECT_SEND_EMAIL)
    private final ProducerTemplate producerTemplate;

    @Async
    public void send(DeliveryEmailDto deliveryEmailDto){
        producerTemplate.sendBody(deliveryEmailDto);
    }
}