package com.neylandev.delivery.domain.service;

import com.neylandev.delivery.domain.enums.DataForBusinessException;
import com.neylandev.delivery.domain.enums.DeliveryStatus;
import com.neylandev.delivery.domain.model.Delivery;
import com.neylandev.delivery.domain.repository.DeliveryRepository;
import com.neylandev.delivery.infrastructure.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.neylandev.delivery.DataForTests.INVALID_DELIVERY_ID;
import static com.neylandev.delivery.DataForTests.deliveryValid;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeliveryCompletionServiceTest {

    @InjectMocks
    private DeliveryCompletionService deliveryCompletionService;

    @Mock
    private DeliveryRepository deliveryRepository;

    @Mock
    private FindDeliveryService findDeliveryService;

    @Test
    void shouldCompleteDelivery() {
        var delivery = deliveryValid();

        when(findDeliveryService.find(delivery.getId())).thenReturn(delivery);

        ArgumentCaptor<Delivery> deliveryArgumentCaptor = ArgumentCaptor.forClass(Delivery.class);
        assertDoesNotThrow(() -> deliveryCompletionService.complete(delivery.getId()));

        verify(deliveryRepository, atLeastOnce()).save(deliveryArgumentCaptor.capture());
        var value = deliveryArgumentCaptor.getValue();
        assertEquals(DeliveryStatus.FINALIZED, value.getDeliveryStatus());
    }

    @Test
    void shouldThrowBusinessException_whenCompleteWasCalled() {
        when(findDeliveryService.find(INVALID_DELIVERY_ID))
                .thenThrow(DataForBusinessException.DELIVERY_NOT_FOUND
                        .asBusinessExceptionWithDescriptionFormatted(Long.toString(INVALID_DELIVERY_ID)));

        assertThrows(BusinessException.class, () -> deliveryCompletionService.complete(INVALID_DELIVERY_ID),
                DataForBusinessException.DELIVERY_NOT_FOUND.getMessage());
    }

    @Test
    void shouldCancelDelivery() {
        var delivery = deliveryValid();

        when(findDeliveryService.find(delivery.getId())).thenReturn(delivery);

        ArgumentCaptor<Delivery> deliveryArgumentCaptor = ArgumentCaptor.forClass(Delivery.class);
        assertDoesNotThrow(() -> deliveryCompletionService.cancel(delivery.getId()));

        verify(deliveryRepository, atLeastOnce()).save(deliveryArgumentCaptor.capture());
        var value = deliveryArgumentCaptor.getValue();
        assertEquals(DeliveryStatus.CANCELLED, value.getDeliveryStatus());
    }

    @Test
    void shouldThrowBusinessException_whenCancelWasCalled() {
        when(findDeliveryService.find(INVALID_DELIVERY_ID))
                .thenThrow(DataForBusinessException.DELIVERY_NOT_FOUND
                        .asBusinessExceptionWithDescriptionFormatted(Long.toString(INVALID_DELIVERY_ID)));

        assertThrows(BusinessException.class, () -> deliveryCompletionService.cancel(INVALID_DELIVERY_ID),
                DataForBusinessException.DELIVERY_NOT_FOUND.getMessage());
    }


}