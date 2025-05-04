package com.ninjaone.dundie_awards.application.api.dundie;

import com.ninjaone.dundie_awards.infrastructure.repository.employee.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

@RestController
class DundieController {
    
    @Autowired
    private EmployeeRepository employeeRepository;

    @PostMapping("/dundie")
    ResponseEntity<Void> giveDundie(@RequestBody GiveDundieRequest request) {
        boolean dundieWasDelivered = employeeRepository.giveDundie(request.employeeId());

        if (!dundieWasDelivered) {
            return notFound().build();
        }

        return ok().build();
    }
}
