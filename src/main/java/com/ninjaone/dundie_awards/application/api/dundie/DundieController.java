package com.ninjaone.dundie_awards.application.api.dundie;

import com.ninjaone.dundie_awards.application.dundie.DundieDeliveryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.ok;

@RestController
class DundieController {

    @Autowired
    private DundieDeliveryManager dundieDeliveryManager;

    @PostMapping("/give-dundie-awards/{organizationId}")
    ResponseEntity<Void> giveDundieAwards(@PathVariable Long organizationId) {
        dundieDeliveryManager.giveDundieAwards(organizationId);
        return ok().build();
    }
}
