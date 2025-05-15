package com.ninjaone.dundie_awards.application.api.index;

import com.ninjaone.dundie_awards.infrastructure.repository.activity.ActivityRepository;
import com.ninjaone.dundie_awards.infrastructure.repository.employee.Employee;
import com.ninjaone.dundie_awards.infrastructure.repository.employee.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.data.domain.Sort.Direction.ASC;

@Controller
@RequestMapping("/")
public class IndexController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @GetMapping()
    public String getIndex(Model model, @PageableDefault(sort = "id", direction = ASC) Pageable pageable) {
        Page<Employee> page = employeeRepository.findAll(pageable);
        model.addAttribute("page", page);
        model.addAttribute("employees", page.getContent());

        model.addAttribute("activities", activityRepository.findAll());
        model.addAttribute("totalDundieAwards", employeeRepository.dundieQuantity());

        return "index";
    }
}