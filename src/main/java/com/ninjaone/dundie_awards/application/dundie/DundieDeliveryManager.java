package com.ninjaone.dundie_awards.application.dundie;

import com.ninjaone.dundie_awards.application.dundie.publisher.DundieDeliverPublisher;
import com.ninjaone.dundie_awards.infrastructure.repository.dundie.delivery.DundieDelivery;
import com.ninjaone.dundie_awards.infrastructure.repository.dundie.delivery.DundieDeliveryRepository;
import com.ninjaone.dundie_awards.infrastructure.repository.organization.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

import static java.util.Optional.empty;
import static java.util.Optional.of;

@Component
public class DundieDeliveryManager {

    @Autowired
    private DundieDeliveryRepository dundieDeliveryRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private DundieDeliverPublisher publisher;

    public Optional<UUID> giveDundieAwards(Long organizationId) {
        boolean organizationDoesNotExists = !organizationRepository.existsById(organizationId);

        if(organizationDoesNotExists) {
            return empty();
        }

        DundieDelivery dundieDelivery = dundieDeliveryRepository.create(organizationId);
        publisher.toDundieDeliverySplitQueue(dundieDelivery.getId());

        return of(dundieDelivery.getIdentifier());
    }
}
