package com.ninjaone.dundie_awards.application.api.employee;

import com.ninjaone.dundie_awards.infrastructure.repository.employee.Employee;
import com.ninjaone.dundie_awards.infrastructure.repository.employee.EmployeeRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping()
    Page<EmployeeResponse> getAllEmployees(@PageableDefault(sort = "id", direction = ASC) Pageable pageable) {
        return employeeRepository.findAll(pageable)
            .map(EmployeeResponse::new);
    }

    @PostMapping()
    EmployeeResponse createEmployee(@Valid @RequestBody CreateEmployeeRequest request) {
        Employee employee = employeeRepository.create(request.firstName(), request.lastName(), request.organizationId());
        return new EmployeeResponse(employee);
    }

    @GetMapping("/{id}")
    ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable Long id) {
        return employeeRepository.findById(id)
            .map(employee -> ok(new EmployeeResponse(employee)))
            .orElse(notFound().build());
    }

    @PutMapping("/{id}")
    ResponseEntity<EmployeeResponse> updateEmployee(@PathVariable Long id, @Valid @RequestBody UpdateEmployeeRequest request) {
        return employeeRepository.updateName(id, request.firstName(), request.lastName())
            .map(employee -> ok(new EmployeeResponse(employee)))
            .orElse(notFound().build());
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        boolean deleted = employeeRepository.delete(id);

        if (!deleted) {
            return notFound().build();
        }

        return ok().build();
    }
}