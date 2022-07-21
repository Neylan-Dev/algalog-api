package com.algaworks.algalog.domain.service;

import com.algaworks.algalog.domain.model.Delivery;
import com.algaworks.algalog.domain.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeliveryCompletionService {

    private final DeliveryRepository deliveryRepository;
    private final FindDeliveryService findDeliveryService;

    @Transactional
    public void complete(Long deliveryId) {
        Delivery delivery = findDeliveryService.find(deliveryId);
        delivery.complete();
        deliveryRepository.save(delivery);
    }

    @Transactional
    public void cancel(Long deliveryId) {
        Delivery delivery = findDeliveryService.find(deliveryId);
        delivery.cancel();
        deliveryRepository.save(delivery);
    }
}