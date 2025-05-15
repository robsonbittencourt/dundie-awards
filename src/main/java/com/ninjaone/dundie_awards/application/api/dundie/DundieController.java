package com.ninjaone.dundie_awards.application.api.dundie;

import com.ninjaone.dundie_awards.application.dundie.DundieDeliveryManager;
import com.ninjaone.dundie_awards.infrastructure.repository.dundie.delivery.DundieDeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

@RestController
class DundieController {

    @Autowired
    private DundieDeliveryManager dundieDeliveryManager;

    @Autowired
    private DundieDeliveryRepository repository;

    @PostMapping("/give-dundie-awards/{organizationId}")
    ResponseEntity<GiveDundieAwardsResponse> giveDundieAwards(@PathVariable Long organizationId) {
        UUID dundieDeliveryId = dundieDeliveryManager.giveDundieAwards(organizationId);
        return ok(new GiveDundieAwardsResponse(dundieDeliveryId));
    }

    @GetMapping("/dundie-delivery/{identifier}")
    ResponseEntity<DundieDeliveryStatusResponse> getByIdentifier(@PathVariable UUID identifier) {
        return repository.findByIdentifier(identifier)
            .map(delivery -> ok(new DundieDeliveryStatusResponse(delivery.getIdentifier(), delivery.getCreatedAt(), delivery.getStatus().name())))
            .orElse(notFound().build());
    }
}
