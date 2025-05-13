package com.ninjaone.dundie_awards.application.dundie;

import com.ninjaone.dundie_awards.application.dundie.publisher.DundieDeliverPublisher;
import com.ninjaone.dundie_awards.infrastructure.repository.dundie.delivery.DundieDelivery;
import com.ninjaone.dundie_awards.infrastructure.repository.dundie.delivery.DundieDeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DundieDeliveryManager {

    @Autowired
    private DundieDeliveryRepository dundieDeliveryRepository;

    @Autowired
    private DundieDeliverPublisher publisher;

    public long giveDundieAwards(Long organizationId) {
        DundieDelivery dundieDelivery = dundieDeliveryRepository.create(organizationId);
        publisher.toDundieDeliverySplitQueue(dundieDelivery);

        return dundieDelivery.getId();
    }
}
